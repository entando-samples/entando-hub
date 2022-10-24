import {OverflowMenu, OverflowMenuItem} from "carbon-components-react"
import { useHistory } from "react-router-dom";
import i18n from "../../../i18n";
import { Settings32 } from '@carbon/icons-react'

const SettingsOverflowMenu = ({}) => {

    const history = useHistory();

    const handleTokenManagementClick = () => {
        history.push("/tokens")
    }

    return (
        <>
            <OverflowMenu renderIcon={Settings32}>
                <OverflowMenuItem 
                itemText={i18n.t('navLink.tokenManagement')}
                onClick={() => handleTokenManagementClick()} 
                 />
            </OverflowMenu>
        </>
    )
}

export default SettingsOverflowMenu
