import { render, fireEvent, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import userEvent from '@testing-library/user-event';

import { ModalAddNewBundleGroup } from './ModalAddNewBundleGroup';
import { useCatalogs } from "../../../contexts/CatalogContext";
import { getHigherRole, isHubAdmin } from '../../../helpers/helpers';
import { addNewBundleGroup } from '../../../integration/Integration';
import { useApiUrl } from '../../../contexts/ConfigContext';

jest.mock('../../../helpers/helpers', () => ({
  getHigherRole: jest.fn(),
  isHubAdmin: jest.fn(),
}));

jest.mock('../../../contexts/ConfigContext', () => ({
  useApiUrl: jest.fn(),
}));

jest.mock('../../../contexts/CatalogContext', () => ({
  useCatalogs: jest.fn(),
}));

jest.mock('../../../integration/Integration', () => ({
  addNewBundleGroup: jest.fn(),
}));

describe('ModalAddNewBundleGroup', () => {
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
  });

  it('submits with the filled values', async () => {
    const onAfterSubmit = jest.fn();
    render(<ModalAddNewBundleGroup onAfterSubmit={onAfterSubmit} catList={[{ name: 'test', categoryId: 'test' }]} />);

    const publicCatalogCheckbox = screen.getByLabelText(/public catalog/i);
    await userEvent.click(publicCatalogCheckbox);

    const nameInput = screen.getByLabelText(/name/i);
    await userEvent.type(nameInput, 'Test Name');

    const categorySelect = screen.getByLabelText(/category/i);
    await userEvent.selectOptions(categorySelect, ['test']);

    const documentationInput = screen.getByLabelText(/documentation address/i);
    await userEvent.type(documentationInput, 'http://documentation.com');

    const versionInput = screen.getByLabelText(/version/i);
    await userEvent.type(versionInput, '1.2.3');

    const statusSelect = screen.getByLabelText(/status/i);
    await userEvent.selectOptions(statusSelect, ['PUBLISH_REQ']);

    const displayContactCheckbox = screen.getByLabelText(/display contact us button/i);
    await userEvent.click(displayContactCheckbox);

    const contactUrlInput = screen.getByLabelText(/contact url/i);
    await userEvent.type(contactUrlInput, 'http://contact.com');

    const descriptionTextarea = screen.getByLabelText(/description/i);
    await userEvent.type(descriptionTextarea, 'Test Description');

    const addButtons = screen.getAllByRole('button', { name: /add/i });
    await userEvent.click(addButtons[addButtons.length - 1]);

    expect(addNewBundleGroup).toHaveBeenCalledWith('http://localhost:8080', expect.objectContaining({
      name: 'Test Name',
      categories: ['test'],
      documentationUrl: 'http://documentation.com',
      contactUrl: 'http://contact.com',
      description: 'Test Description',
      publicCatalog: true,
      versionDetails: expect.objectContaining({
        displayContactUrl: true,
        description: 'Test Description',
        documentationUrl: 'http://documentation.com',
        version: '1.2.3',
        status: 'PUBLISH_REQ',
      }),
    }));
    expect(onAfterSubmit).toHaveBeenCalled();
  });

  it('shows an error message when name input exceeds the character limit', async () => {
    render(<ModalAddNewBundleGroup />);
    const nameInput = screen.getByLabelText(/name/i);

    const maxLength = 25;
    const overLimitValue = 'a'.repeat(maxLength + 1);
    await userEvent.type(nameInput, overLimitValue);

    expect(screen.getByText(/The name must not exceed 25 characters/i)).toBeInTheDocument();
  });

  it('shows an error message when contact URL input is invalid', async () => {
    render(<ModalAddNewBundleGroup />);
    const displayContactUrlCheckbox = screen.getByLabelText(/display contact us button/i);
    fireEvent.click(displayContactUrlCheckbox);

    const contactUrlInput = screen.getByLabelText(/contact url/i);
    await userEvent.type(contactUrlInput, 'invalid-url');
    fireEvent.blur(contactUrlInput);

    expect(screen.getByText(/Please provide a valid contact URL/i)).toBeInTheDocument();
  });

  it('shows an error message when documentation URL input is invalid', async () => {
    render(<ModalAddNewBundleGroup />);
    const documentationInput = screen.getByLabelText(/documentation address/i);
    await userEvent.type(documentationInput, 'invalid-url');
    fireEvent.blur(documentationInput);

    expect(screen.getByText(/Documentation must match URL format/i)).toBeInTheDocument();
  });

  it('shows an error message when version input is invalid', async () => {
    render(<ModalAddNewBundleGroup />);
    const versionInput = screen.getByLabelText(/version/i);
    await userEvent.type(versionInput, '1.0...3');
    fireEvent.blur(versionInput);

    expect(screen.getByText(/version must match semantic versioning format/i)).toBeInTheDocument();
  });
});
