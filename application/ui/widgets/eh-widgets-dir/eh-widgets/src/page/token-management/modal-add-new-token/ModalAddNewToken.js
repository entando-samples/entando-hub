import {Button, ComposedModal, ModalBody, ModalFooter, ModalHeader} from "carbon-components-react"
import {Add16} from '@carbon/icons-react'
import ReactDOM from "react-dom"
import {useState} from "react"
import NewToken from "./new-token/NewToken"
import {addNewToken} from "../../../integration/Integration"
import "./modal-add-new-token.scss"
import { tokenSchema } from "../../../helpers/validation/tokenSchema"
import { fillErrors } from "../../../helpers/validation/fillErrors"
import i18n from "../../../i18n"
export const ModalAddNewToken = ({onAfterSubmit}) => {

    const ModalStateManager = ({
                                   renderLauncher: LauncherContent,
                                   children: ModalContent,
                               }) => {
        const [open, setOpen] = useState(false)
        const [elemKey, setElemKey] = useState(((new Date()).getTime()).toString()) //to clear form data
        const [token, setToken] = useState({})
        const [validationResult, setValidationResult] = useState({})

        const onDataChange = (newToken)=>{
            setToken(newToken)
        }

        const onRequestClose = (e) =>{
            resetData()
            setOpen(false)
        }

        const onRequestOpen = (e) =>{
            setOpen(true)
        }

        const resetData = ()=>{
            setElemKey(((new Date()).getTime()).toString())
            setToken({})
            setValidationResult({})
        }

        //Manage the modal submit
        const onRequestSubmit = (e) => {
            (async () => {
                let validationError
                await tokenSchema.validate(token, {abortEarly: false}).catch(error => {
                    validationError = fillErrors(error)
                })
                if (validationError) {
                    setValidationResult(validationError)
                    return //don't send the form
                }
                await addNewToken(token)
                onRequestClose()
                onAfterSubmit()
            })()
        }

        return (
            <>
                {!ModalContent || typeof document === 'undefined'
                    ? null
                    : ReactDOM.createPortal(
                        <ModalContent validationResult={validationResult} open={open} onRequestClose={onRequestClose} onDataChange={onDataChange} onRequestSubmit={onRequestSubmit} elemKey={elemKey}/>,
                        document.body
                    )}
                {LauncherContent && <LauncherContent onRequestOpen={onRequestOpen}/>}
            </>
        )
    }

    return (
        <ModalStateManager
            renderLauncher={({onRequestOpen}) => (
                <Button onClick={onRequestOpen} renderIcon={Add16}>{i18n.t('component.button.addToken')}</Button>
            )}>
            {({open, onRequestClose, onDataChange, onRequestSubmit, elemKey, validationResult}) => (
                <ComposedModal
                    className="Modal-Add-New-token"
                    open={open}
                    onClose={onRequestClose}
                >
                    <ModalHeader label={i18n.t('component.button.addToken')} />
                    <ModalBody>
                        <NewToken key={elemKey} onDataChange={onDataChange} validationResult={validationResult} />
                    </ModalBody>
                    <ModalFooter>
                        <Button
                            kind="secondary"
                            onMouseDown={() => { onRequestClose() }}>
                            {i18n.t('component.button.cancel')}
                        </Button>
                        <Button
                            kind="primary"
                            onClick={() => { onRequestSubmit() }}>
                            {i18n.t('component.button.save')}
                        </Button>
                    </ModalFooter>
                </ComposedModal>
            )}
        </ModalStateManager>
    )
}
