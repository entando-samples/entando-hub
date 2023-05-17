import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { ModalDeleteBundleGroup } from './ModalDeleteBundleGroup';
import { useApiUrl } from '../../../../contexts/ConfigContext';
import { deleteBundleGroupVersion } from '../../../../integration/Integration';

jest.mock('../../../../integration/Integration', () => ({
  deleteBundleGroupVersion: jest.fn(),
}));

jest.mock('../../../../contexts/ConfigContext', () => ({
  useApiUrl: jest.fn(),
}));

describe('ModalDeleteBundleGroup', () => {
  it('calls the correct function on "Delete" button click', async () => {
    useApiUrl.mockReturnValue('http://localhost:8080');
    const onAfterSubmit = jest.fn();
    const onCloseModal = jest.fn();

    render(
      <ModalDeleteBundleGroup
        bundleGroupVersionId={1}
        onAfterSubmit={onAfterSubmit}
        onCloseModal={onCloseModal}
        open
      />
    );

    const deleteButton = screen.getByRole('button', { name: /delete/i });
    await userEvent.click(deleteButton);

    expect(deleteBundleGroupVersion).toHaveBeenCalledWith('http://localhost:8080', 1);
    expect(onAfterSubmit).toHaveBeenCalled();
    expect(onCloseModal).toHaveBeenCalled();
  });
});
