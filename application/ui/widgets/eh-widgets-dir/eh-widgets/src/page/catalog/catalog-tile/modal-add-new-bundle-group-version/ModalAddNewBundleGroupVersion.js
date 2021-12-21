import { Loading, Modal } from "carbon-components-react"
import { useCallback, useEffect, useState } from "react"
import {
  addNewBundle,
  addNewBundleGroupVersion,
  editBundleGroup,
  getAllBundlesForABundleGroup,
  getAllCategories,
  getSingleOrganisation,
} from "../../../../integration/Integration"
import { getProfiledUpdateSelectStatusInfo } from "../../../../helpers/profiling"
import { getHigherRole } from "../../../../helpers/helpers"
import {
  newVersionBundleGroupSchema,
} from "../../../../helpers/validation/newVersionBundleGroupSchema"
import { fillErrors } from "../../../../helpers/validation/fillErrors"
import { BUNDLE_STATUS, BUTTON_LABELS, DUPLICATE_VERSION, MODAL_LABELS } from "../../../../helpers/constants"

import "./modal-add-new-bundle-group-version.scss"
import BundleGroupVersionForm from "../../../../components/forms/BundleGroupVersionForm/BundleGroupVersionForm"
import i18n from "../../../../i18n"
import { isVersionDuplicate } from "../../../../helpers/validation/isVersionDuplicateValidate"

export const ModalAddNewBundleGroupVersion = ({
  theBundleGroup,
  open,
  onCloseModal,
  onAfterSubmit,
}) => {
  const [allowedOrganisations, setAllowedOrganisations] = useState([{
    organisationId: "",
    name: "",
  }])
  const [categories, setCategories] = useState([])

  const [bundleGroup, setBundleGroup] = useState({})
  const [passiveModal, setPassiveModal] = useState(false)
  const [loading, setLoading] = useState(true)

  const [selectStatusValues, setSelectStatusValues] = useState([])
  const [validationResult, setValidationResult] = useState({})
  const [minOneBundleError, setMinOneBundleError] = useState("")


  const onDataChange = useCallback((bundleGroup) => {
    setBundleGroup(bundleGroup)
  }, [])

  const onRequestClose = (e) => {
    onCloseModal()
    setValidationResult({})
  }

  useEffect(() => {
    let isMounted = true
    setLoading(true)

    const initCG = async () => {
      const res = await getAllCategories()
      if (isMounted) {
        setCategories(res.categoryList)
      }
    }
    const initBG = async () => {
      // -------------kamlesh----
      const childrenFromDb = theBundleGroup 
                              && theBundleGroup.children 
                              && theBundleGroup.children.length > 0 
                              && theBundleGroup.bundleGroupId
          ? (await getAllBundlesForABundleGroup(theBundleGroup.bundleGroupId)).bundleList
          : [];
          
        const bundleGroupOrganisation = (await getSingleOrganisation(theBundleGroup && theBundleGroup.organisationId)).organisation;
        if (isMounted) {
          if (bundleGroupOrganisation) {
            setAllowedOrganisations([bundleGroupOrganisation])
          }
          let bg = {
            ...theBundleGroup,
            children: childrenFromDb,
          }
          const selectStatusValues = getProfiledUpdateSelectStatusInfo(
            getHigherRole(),
            bg.status
          )
          setSelectStatusValues(selectStatusValues);
          setPassiveModal(selectStatusValues.disabled);
          setBundleGroup(bg);
        }
    }

    ;(async () => {
      await Promise.all([initCG(), initBG()])
      setLoading(false)
    })()
    return () => {
      isMounted = false
    }
  }, [theBundleGroup])

  //TODO BE QUERY REFACTORING ---- Remove below method
  const updateBundleGroup = async (bundleGroup) => {
    let newChildren = []
    if (bundleGroup.children && bundleGroup.children.length) {
      //call addNewBundle rest api, saving every bundle
      //WARNING a new bundle is created even if already exists
      //the call is async in respArray there will be the new bundles id
      let respArray = await Promise.all(bundleGroup.children.map(addNewBundle))
      newChildren = respArray.map((res) => res && res.newBundle && res.newBundle.data && res.newBundle.data.bundleId)
    }
    const toSend = {
      ...bundleGroup,
      children: newChildren,
    }
    await editBundleGroup(toSend, toSend.bundleGroupId)
  }

// -----------------------------kamlesh
  //Add Bundle Group Version api call
  const addBundleGroupVersion = async(bundleGroupVersion) => {
    let newChildren = []
    if (bundleGroupVersion.children && bundleGroupVersion.children.length) {
      //call addNewBundle rest api, saving every bundle
      //WARNING a new bundle is created even if already exists
      //the call is async in respArray there will be the new bundles id
      let respArray = await Promise.all(bundleGroupVersion.children.map(addNewBundle))
      newChildren = respArray.map((res) => res && res.newBundle && res.newBundle.data && res.newBundle.data.bundleId)
    }
    const toSend = {
      ...bundleGroup,
      children: newChildren,
    }
    await addNewBundleGroupVersion(toSend);
  }

  const onRequestSubmit = (e) => {
    
    ;(async () => {
      // Useful code for validation: EHUB-147:
      let validationError
      await newVersionBundleGroupSchema.validate(bundleGroup, { abortEarly: false })
        .catch((err) => {
          validationError = fillErrors(err)
        })

      if (isVersionDuplicate(bundleGroup.version, theBundleGroup.allVersions)) {
        let versionValidationError = (validationError && validationError.version) ? [...validationError.version, i18n.t('formValidationMsg.duplicateVersion')] : [i18n.t('formValidationMsg.duplicateVersion')]
        if (!validationError) {
          validationError = { version: versionValidationError }
        }
        validationError.version = versionValidationError
      }
      if ((bundleGroup && (bundleGroup.status === BUNDLE_STATUS.NOT_PUBLISHED || bundleGroup.status === BUNDLE_STATUS.DELETE_REQ)) &&
        validationError && validationError.children && validationError.children.length === 1 &&
        Object.keys(validationError).length === 1) {
        validationError = undefined;
      }
      if (bundleGroup && bundleGroup.children && bundleGroup.children.length === 0 &&
        (bundleGroup.status === BUNDLE_STATUS.PUBLISH_REQ || bundleGroup.status === BUNDLE_STATUS.PUBLISHED)) {
        setMinOneBundleError(validationError.children[0]);
      }
      if (validationError) {
        setValidationResult(validationError)
        return
      }
      // await updateBundleGroup(bundleGroup)
      await addBundleGroupVersion(bundleGroup)
      onCloseModal()
      onAfterSubmit()
    })()
  }

  return (
    <>
      {loading && <Loading />}
      {!loading &&
        <Modal
          passiveModal={passiveModal}
          className="Modal-edit-bundle-group"
          modalLabel={i18n.t('modalMsg.addNewVersion')}
          primaryButtonText={i18n.t('component.button.submit')}
          secondaryButtonText={i18n.t('component.button.cancel')}
          open={open}
          onRequestClose={onRequestClose}
          onRequestSubmit={onRequestSubmit}
        >
          <BundleGroupVersionForm
            allowedOrganisations={allowedOrganisations}
            categories={categories}
            onDataChange={onDataChange}
            bundleGroup={bundleGroup}
            selectStatusValues={selectStatusValues}
            validationResult={validationResult}
            minOneBundleError={minOneBundleError}
            allVersions={theBundleGroup.allVersions}
            mode="Edit"
          />
        </Modal>
      }
    </>
  )
}