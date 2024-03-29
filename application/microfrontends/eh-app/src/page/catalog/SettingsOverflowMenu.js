import { OverflowMenu, OverflowMenuItem } from 'carbon-components-react'
import { Settings20 as SettingsIcon } from '@carbon/icons-react';
import { useHistory } from 'react-router-dom';

import i18n from '../../i18n';

const SettingsOverflowMenu = () => {
  const history = useHistory();

  const handleApiKeyManagementClick = () => {
    history.push('/apikeys');
  };

  return (
    <OverflowMenu renderIcon={SettingsIcon}>
      <OverflowMenuItem
        itemText={i18n.t('navLink.apiKeyManagement')}
        onClick={handleApiKeyManagementClick}
      />
    </OverflowMenu>
  )
};

export default SettingsOverflowMenu;
