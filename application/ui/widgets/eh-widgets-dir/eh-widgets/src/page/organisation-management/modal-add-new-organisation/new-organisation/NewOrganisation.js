import { Content, TextInput } from "carbon-components-react"
import { useEffect, useRef, useState } from "react"
import { CHAR_LENGTH, CHAR_LIMIT_MSG_SHOW_TIME, DESCRIPTION_FIELD_ID, MAX_CHAR_LENGTH, MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM, NAME_FIELD_ID } from "../../../../helpers/constants"
import { organisationSchema } from "../../../../helpers/validation/organisationSchema"
import i18n from "../../../../i18n"

const NewOrganisation = ({ onDataChange, validationResult }) => {
  const [organisation, setOrganisation] = useState({
    name: "",
    description: "",
  })
  const [orgNameLength, setOrgNameLength] = useState(false);
  const [orgDescLength, setOrgDescLength] = useState(false);

  const [mounted, setMounted] = useState(false);
  const timerRef = useRef(null);

  const [showNameCharLimitErrMsg, setShowNameCharLimitErrMsg] = useState(false);
  const [showDescriptionCharLimitErrMsg, setShowDescriptionCharLimitErrMsg] = useState(false);

  const changeOrganisation = (field, value) => {
    const newObj = {
      ...organisation,
    }
    newObj[field] = value
    setOrganisation(newObj)
    onDataChange(newObj)
  }

  /**
   * @param {*} e Event object to get value of field
   * @param {*} fieldName Name of the field
   * @description Validation & Setting on fields.
   */
  const onChangeHandler = (e, fieldName) => {
    if (fieldName === DESCRIPTION_FIELD_ID && e.target.value.trim().length) {
      const msg = e.target.value.trim().length > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM ? i18n.t('formValidationMsg.description') : ""
      validationResult["description"] = [msg]
      setOrgDescLength(e.target.value.trim().length)
    }

    if (fieldName === NAME_FIELD_ID) {
      if (e.target.value.trim().length < CHAR_LENGTH) {
        const msg = e.target.value.trim().length === 0 ? i18n.t('formValidationMsg.nameRequired') : i18n.t('formValidationMsg.min3Char')
        validationResult["name"] = [msg]
      }
      if (e.target.value.trim().length > MAX_CHAR_LENGTH) {
        validationResult["name"] = [i18n.t('formValidationMsg.max25Char')]
      }
      setOrgNameLength(e.target.value.trim().length)
    }
    changeOrganisation(fieldName, e.target.value)
  }

  /**
   * Handle keyPress event for input fields and show/hide character limit error message
   * @param {*} e
   */
   const keyPressHandler = (e) => {
    if (e.target.id === NAME_FIELD_ID && e.target.value.length >= MAX_CHAR_LENGTH) {
      validationResult[NAME_FIELD_ID] = [i18n.t('formValidationMsg.max25Char')];
      setShowNameCharLimitErrMsg(true);
      timerRef.current = setTimeout(() => disappearCharLimitErrMsg(e.target.id), CHAR_LIMIT_MSG_SHOW_TIME);
    } else if (e.target.id === DESCRIPTION_FIELD_ID && e.target.value.length >= MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM) {
      validationResult[DESCRIPTION_FIELD_ID] = [i18n.t('formValidationMsg.description')];
      setShowDescriptionCharLimitErrMsg(true);
      timerRef.current = setTimeout(() => disappearCharLimitErrMsg(e.target.id), CHAR_LIMIT_MSG_SHOW_TIME);
    }
  }

  const disappearCharLimitErrMsg = (fieldId) => {
    if (mounted) {
      validationResult[fieldId] = undefined;
      if (fieldId === NAME_FIELD_ID) {
        setShowNameCharLimitErrMsg(false);
      } else if (fieldId === DESCRIPTION_FIELD_ID) {
        setShowDescriptionCharLimitErrMsg(false);
      }
    }
  }

  useEffect(() => {
    setMounted(true);
    // Clear the interval when the component unmounts
    return () => {
      setMounted(false);
      clearTimeout(timerRef.current);
    };
  }, []);

  /**
   * @param {*} e Event object to get value of field
   * @param {*} field Name of the field
   * @description Trimming whitespaces from the field value.
   */
  const trimBeforeFormSubmitsHandler = (e, field) => {
    changeOrganisation(field, e.target.value.trim())
  }

  return (
    <>
      <Content>
        <TextInput
        invalid={(orgNameLength < CHAR_LENGTH || orgNameLength > MAX_CHAR_LENGTH || showNameCharLimitErrMsg) && !!validationResult["name"]}
          invalidText={
            ((orgNameLength < CHAR_LENGTH || orgNameLength > MAX_CHAR_LENGTH || showNameCharLimitErrMsg)) ? (validationResult["name"] && validationResult["name"].join("; ")) : null
          }
          id={NAME_FIELD_ID}
          value={organisation.name}
          labelText={`${i18n.t('page.management.label.name')} ${organisationSchema.fields.name.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "name")}
          maxLength={MAX_CHAR_LENGTH}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "name")}
          onKeyPress={keyPressHandler}
        />
        <TextInput
          invalid={(orgDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM || showDescriptionCharLimitErrMsg) && !!validationResult["description"]}
          invalidText={
            (orgDescLength > MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM || showDescriptionCharLimitErrMsg) && (validationResult["description"] && validationResult["description"].join("; "))
          }
          id={DESCRIPTION_FIELD_ID}
          value={organisation.description}
          labelText={`${i18n.t('page.management.label.description')} ${organisationSchema.fields.description.exclusiveTests.required ? " *" : ""}`}
          onChange={(e) => onChangeHandler(e, "description")}
          maxLength={MAX_CHAR_LENGTH_FOR_DESC_CATEGORY_AND_ORG_FORM}
          onBlur={(e) => trimBeforeFormSubmitsHandler(e, "description")}
          onKeyPress={keyPressHandler}
        />
      </Content>
    </>
  )
}
export default NewOrganisation
