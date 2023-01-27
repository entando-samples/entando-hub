import { OverflowMenu, OverflowMenuItem } from "carbon-components-react"
import { useState } from "react"
import { ModalUpdateOrganisation } from "../modal-update-organisation/ModalUpdateOrganisation"
import {
  getSingleOrganisation,
  deleteOrganisation,
} from "../../../integration/Integration"
import i18n from "../../../i18n"

const OrganisationManagementOverflowMenu = ({
  apiUrl,
  organisationObj,
  onAfterSubmit,
  setReloadToken,
}) => {
  const [openModal, setOpenModal] = useState(false)

  const deleteHandler = async () => {
    const org = await getSingleOrganisation(apiUrl,organisationObj.organisationId)

    await deleteOrganisation(apiUrl,org.organisation.organisationId)

    setReloadToken(new Date().getTime().toString())
  }

  return (
    <>
      <OverflowMenu>
        <OverflowMenuItem itemText={i18n.t('component.button.edit')} onClick={() => setOpenModal(true)} />
        <OverflowMenuItem itemText={i18n.t('component.button.delete')} onClick={deleteHandler} />
      </OverflowMenu>
      {openModal && (
        <ModalUpdateOrganisation
          apiUrl={apiUrl}
          organisationObj={organisationObj}
          open={openModal}
          onCloseModal={() => setOpenModal(false)}
          onAfterSubmit={onAfterSubmit}
        />
      )}
    </>
  )
}

export default OrganisationManagementOverflowMenu
