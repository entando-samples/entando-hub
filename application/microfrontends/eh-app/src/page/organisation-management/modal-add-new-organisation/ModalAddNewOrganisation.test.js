import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ModalAddNewOrganisation } from './ModalAddNewOrganisation';
import { addNewOrganisation } from '../../../integration/Integration';
import { useApiUrl } from '../../../contexts/ConfigContext';

jest.mock('../../../integration/Integration', () => ({
  addNewOrganisation: jest.fn(),
}));

jest.mock('../../../contexts/ConfigContext', () => ({
  useApiUrl: jest.fn(),
}));

describe('ModalAddNewOrganisation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    useApiUrl.mockReturnValue('http://localhost:8080');
  });

  it('submits the form with valid input values', async () => {
    const mockSubmit = jest.fn();
    addNewOrganisation.mockResolvedValue({});

    render(<ModalAddNewOrganisation onAfterSubmit={mockSubmit} />);
    await userEvent.click(screen.getByRole('button', { name: /add organisation/i }));
    await userEvent.type(screen.getByLabelText(/name/i), 'Test Organisation');
    await userEvent.type(screen.getByLabelText(/description/i), 'This is a test organisation');
    await userEvent.click(screen.getByRole('button', { name: /add$/i }));
  
    expect(addNewOrganisation).toHaveBeenCalledWith('http://localhost:8080', {
      name: 'Test Organisation',
      description: 'This is a test organisation',
    });
    expect(mockSubmit).toHaveBeenCalled();
  });

  it('shows an error message when name input exceeds the character limit', async () => {
    render(<ModalAddNewOrganisation />);
    const nameInput = screen.getByLabelText(/name/i);

    const maxLength = 25;
    const overLimitValue = 'a'.repeat(maxLength + 1);
    await userEvent.type(nameInput, overLimitValue);

    expect(screen.getByText(/The name must not exceed 25 characters/i)).toBeInTheDocument();
  });

  it('shows an error message when description input exceeds the character limit', async () => {
    render(<ModalAddNewOrganisation />);
    const nameInput = screen.getByLabelText(/description/i);

    const maxLength = 100;
    const overLimitValue = 'a'.repeat(maxLength + 1);
    await userEvent.type(nameInput, overLimitValue);

    expect(screen.getByText(/Description must not exceed 100 characters/i)).toBeInTheDocument();
  });
});
