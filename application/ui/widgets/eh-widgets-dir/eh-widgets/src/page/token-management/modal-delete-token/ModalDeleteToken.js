import { Modal } from "carbon-components-react"
import { TrashCan32 } from "@carbon/icons-react"
import "./modal-delete-token.scss"
import { deleteToken } from "../../../integration/Integration"
import i18n from "../../../i18n"

export const ModalDeleteToken = ({
   tokenObj,
    open,
    onCloseModal,
    onAfterSubmit,
    tokens
}) => {

    const bundleGroupsLengthOfActiveToken = categories.find(item => item.id === tokenObj.tokenId).bundleGroups.length;

    const onRequestClose = (e) => {
        onCloseModal()
    }

    const onRequestDelete = async (e) => {
        await deleteToken(tokenObj.tokenId, tokenObj.name)
        onCloseModal()
        onAfterSubmit()
    }

    if (bundleGroupsLengthOfActiveToken) {
        return (<Modal
            open
            passiveModal
            onRequestClose={onRequestClose}
            modalHeading={i18n.t("modalMsg.impossibleToRemoveToken")}></Modal>)
    }

    return (<Modal
        className="Modal-Update-token"
        modalLabel={i18n.t('component.button.delete')}
        primaryButtonText={i18n.t('component.button.delete')}
        secondaryButtonText={i18n.t('component.button.cancel')}
        open={open}
        onRequestClose={onRequestClose}
        onRequestSubmit={onRequestDelete}
    >
        <div className="Modal-delete-token-group">
            <div className="Modal-delete-token-group-wrapper">
                <TrashCan32 />
            </div>
            <div>
                {i18n.t("modalMsg.deleteToken")}
            </div>
        </div>
    </Modal>)
}

export default ModalDeleteToken;