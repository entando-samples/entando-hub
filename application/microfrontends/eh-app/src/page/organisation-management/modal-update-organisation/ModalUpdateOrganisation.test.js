import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ModalUpdateOrganisation } from './ModalUpdateOrganisation';
import { editOrganisation, getSingleOrganisation } from '../../../integration/Integration';
import { useApiUrl } from '../../../contexts/ConfigContext';

jest.mock('../../../integration/Integration', () => ({
  editOrganisation: jest.fn(),
  getSingleOrganisation: jest.fn(),
}));

jest.mock('../../../contexts/ConfigContext', () => ({
  useApiUrl: jest.fn(),
}));

describe('ModalUpdateOrganisation', () => {
  const organisation = {
    organisationId: 1,
    name: 'Test Organisation',
    description: 'This is a test organisation',
  };

  beforeEach(() => {
    jest.clearAllMocks();

    useApiUrl.mockReturnValue('http://localhost:8080');
    getSingleOrganisation.mockResolvedValue({ organisation: { ...organisation, bundleGroups: [] } });
  });

  it.skip('submits the form with valid input values', async () => {
    const mockSubmit = jest.fn();
    const mockCloseModal = jest.fn();

    render(
      <ModalUpdateOrganisation
        organisationObj={organisation}
        onAfterSubmit={mockSubmit}
        onCloseModal={mockCloseModal}
        open
      />
    );
    await userEvent.type(screen.getByLabelText(/name/i), ' updated');
    await userEvent.type(screen.getByLabelText(/description/i), ' updated');
    await userEvent.click(screen.getByRole('button', { name: /save/i }));
  
    expect(editOrganisation).toHaveBeenCalledWith('http://localhost:8080', {
      name: 'Test Organisation updated',
      description: 'This is a test organisation updated',
      bundleGroups: [],
    }, 1);
    expect(mockSubmit).toHaveBeenCalled();
    expect(mockCloseModal).toHaveBeenCalled();
  });

  it('shows an error message when name input exceeds the character limit', async () => {
    render(<ModalUpdateOrganisation organisationObj={organisation} open />);
    const nameInput = screen.getByLabelText(/name/i);

    const maxLength = 25;
    const overLimitValue = 'a'.repeat(maxLength + 1);
    await userEvent.type(nameInput, overLimitValue);

    expect(screen.getByText(/The name must not exceed 25 characters/i)).toBeInTheDocument();
  });

  it('shows an error message when description input exceeds the character limit', async () => {
    render(<ModalUpdateOrganisation organisationObj={organisation} open />);
    const nameInput = screen.getByLabelText(/description/i);

    const maxLength = 100;
    const overLimitValue = 'a'.repeat(maxLength + 1);
    await userEvent.type(nameInput, overLimitValue);

    expect(screen.getByText(/Description must not exceed 100 characters/i)).toBeInTheDocument();
  });
});
