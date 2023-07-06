import { render, fireEvent, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import userEvent from '@testing-library/user-event';

import { ModalUpdateBundleGroup } from './ModalUpdateBundleGroup';
import { useCatalogs } from "../../../../contexts/CatalogContext";
import { getHigherRole, isHubAdmin } from '../../../../helpers/helpers';
import { useApiUrl } from '../../../../contexts/ConfigContext';
import { editBundleGroup, getAllCategories, getSingleOrganisation } from '../../../../integration/Integration';

jest.mock('../../../../helpers/helpers', () => ({
  getHigherRole: jest.fn(),
  isHubAdmin: jest.fn(),
}));

jest.mock('../../../../contexts/ConfigContext', () => ({
  useApiUrl: jest.fn(),
}));

jest.mock('../../../../contexts/CatalogContext', () => ({
  useCatalogs: jest.fn(),
}));

jest.mock('../../../../integration/Integration', () => ({
  editBundleGroup: jest.fn(),
  getAllCategories: jest.fn(),
  getSingleOrganisation: jest.fn(),
}));

describe('ModalUpdateBundleGroup', () => {
  const bundleGroup = {
    isEditable: true,
    bundleGroupId: 1,
    name: 'Test Name',
    categories: ['test'],
    description: 'Test Description',
    publicCatalog: true,
    contactUrl: 'http://contact.com',
    documentationUrl: 'http://documentation.com',
    displayContactUrl: true,
    versionDetails: {
      description: 'Test Description',
      documentationUrl: 'http://documentation.com',
      version: '1.2.3',
      status: 'PUBLISH_REQ',
    },
  };

  beforeEach(() => {
    getHigherRole.mockReturnValue('eh-admin');
    isHubAdmin.mockReturnValue(true);
    useApiUrl.mockReturnValue('http://localhost:8080');
    useCatalogs.mockReturnValue({
      catalogs: [
        { id: 1, name: "catalog1" },
        { id: 2, name: "catalog2" },
      ],
    });

    getAllCategories.mockResolvedValue({ categoryList: [{ name: 'test', id: 'test' }] });
    getSingleOrganisation.mockResolvedValue({ organisation: { name: 'test', id: 1 } });
  });

  it.skip('submits with the filled values', async () => {
    const onAfterSubmit = jest.fn();
    const onCloseModal = jest.fn();
    render(
      <ModalUpdateBundleGroup
        bundleGroupId={1}
        bundleGroupObj={bundleGroup}
        onAfterSubmit={onAfterSubmit}
        catList={[{ name: 'test', categoryId: 'test' }]}
        onCloseModal={onCloseModal}
        open
      />
    );

    const versionInput = await screen.findByLabelText(/version/i);
    await userEvent.type(versionInput, '1.2.4');

    const statusSelect = await screen.findByLabelText(/status/i);
    await userEvent.selectOptions(statusSelect, ['NOT_PUBLISHED']);

    const saveButton = screen.getByRole('button', { name: /save/i });
    await userEvent.click(saveButton);

    expect(editBundleGroup).toHaveBeenCalledWith('http://localhost:8080', expect.objectContaining({
      name: 'Test Name',
      categories: ['test'],
      publicCatalog: true,
      versionDetails: expect.objectContaining({
        contactUrl: 'http://contact.com',
        documentationUrl: 'http://documentation.com',
        displayContactUrl: true,
        description: 'Test Description',
        version: '1.2.4',
        status: 'NOT_PUBLISHED',
      }),
    }), 1);
    expect(onAfterSubmit).toHaveBeenCalled();
    expect(onCloseModal).toHaveBeenCalled();
  });

  it('shows an error message when name input exceeds the character limit', async () => {
    render(<ModalUpdateBundleGroup bundleGroupObj={bundleGroup} open />);
    const nameInput = await screen.findByLabelText(/name/i);

    const maxLength = 25;
    const overLimitValue = 'a'.repeat(maxLength + 1);
    await userEvent.type(nameInput, overLimitValue);

    expect(screen.getByText(/The name must not exceed 25 characters/i)).toBeInTheDocument();
  });

  it('shows an error message when contact URL input is invalid', async () => {
    render(<ModalUpdateBundleGroup bundleGroupObj={bundleGroup} open />);

    const contactUrlInput = await screen.findByLabelText(/contact url/i);
    await userEvent.type(contactUrlInput, 'invalid-url');
    fireEvent.blur(contactUrlInput);

    expect(screen.getByText(/Please provide a valid contact URL/i)).toBeInTheDocument();
  });

  it('shows an error message when documentation URL input is invalid', async () => {
    render(<ModalUpdateBundleGroup bundleGroupObj={bundleGroup} open />);
    const documentationInput = await screen.findByLabelText(/documentation address/i);
    await userEvent.type(documentationInput, 'invalid-url');
    fireEvent.blur(documentationInput);

    expect(screen.getByText(/Documentation must match URL format/i)).toBeInTheDocument();
  });

  it('shows an error message when version input is invalid', async () => {
    render(<ModalUpdateBundleGroup bundleGroupObj={bundleGroup} open />);
    const versionInput = await screen.findByLabelText(/version/i);
    await userEvent.type(versionInput, '1.0...3');
    fireEvent.blur(versionInput);

    expect(screen.getByText(/version must match semantic versioning format/i)).toBeInTheDocument();
  });
});
