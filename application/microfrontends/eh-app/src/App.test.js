import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import App from './App';
import { isHubAdmin } from './helpers/helpers';

jest.mock('./page/catalog/CatalogPage', () => () => <div>CatalogPage</div>);
jest.mock('./page/bundle-group/BundleGroupPage', () => () => <div>BundleGroupPage</div>);
jest.mock('./page/user-management/UserManagementPage', () => () => <div>UserManagementPage</div>);
jest.mock('./page/organisation-management/OrganisationManagementPage', () => () => <div>OrganisationManagementPage</div>);
jest.mock('./page/category-management/CategoryManagementPage', () => () => <div>CategoryManagementPage</div>);
jest.mock('./components/notification/NotificationDispatcher', () => () => <div>NotificationDispatcher</div>);
jest.mock('./page/bundle-group-version/bg-version-catalog/BundleGroupVersionsPage', () => () => <div>BundleGroupVersionsPage</div>);
jest.mock('./page/api-key-management/ApiKeyManagementPage', () => () => <div>ApiKeyManagementPage</div>);
jest.mock('./components/errors/NotFound', () => () => <div>NotFound</div>);
jest.mock('./contexts/CatalogContext', () => ({
  CatalogProvider: ({children}) => <div>{children}</div>
}));
jest.mock('./helpers/helpers', () => ({
  isHubAdmin: jest.fn(),
}));

describe('App Component', () => {
  it('should render BundleGroupPage on "/bundlegroup/:id" route', () => {
    render(
      <MemoryRouter initialEntries={['/bundlegroup/1']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText('BundleGroupPage')).toBeInTheDocument();
  });

  it('should render BundleGroupPage on "/bundlegroup/versions/:id" route', () => {
    render(
      <MemoryRouter initialEntries={['/bundlegroup/versions/1']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText('BundleGroupPage')).toBeInTheDocument();
  });

  it('should render BundleGroupPage on "/catalog/:catalogId/bundlegroup/versions/:id" route', () => {
    render(
      <MemoryRouter initialEntries={['/catalog/1/bundlegroup/versions/1']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText('BundleGroupPage')).toBeInTheDocument();
  });

  it('should render BundleGroupPage on "/catalog/:catalogId/bundlegroup/:id" route', () => {
    render(
      <MemoryRouter initialEntries={['/catalog/1/bundlegroup/1']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText('BundleGroupPage')).toBeInTheDocument();
  });
  
  it('should render ApiKeyManagementPage on "/apikeys" route', () => {
    render(
      <MemoryRouter initialEntries={['/apikeys']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText('ApiKeyManagementPage')).toBeInTheDocument();
  });
  
  it("should render UserManagementPage when gateFunction returns true and path is '/admin'", () => {
    isHubAdmin.mockReturnValueOnce(true);
  
    render(
      <MemoryRouter initialEntries={['/admin']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText("UserManagementPage")).toBeInTheDocument();
  });

  it('should render OrganisationManagementPage when gateFunction returns true and path is "/organisations"', () => {
    isHubAdmin.mockReturnValueOnce(true);
  
    render(
      <MemoryRouter initialEntries={['/organisations']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText("OrganisationManagementPage")).toBeInTheDocument();
  });

  it('should render CategoryManagementPage when gateFunction returns true and path is "/categories"', () => {
    isHubAdmin.mockReturnValueOnce(true);
  
    render(
      <MemoryRouter initialEntries={['/categories']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText("CategoryManagementPage")).toBeInTheDocument();
  });
  
  it('should render NotFound on "/404" route', () => {
    render(
      <MemoryRouter initialEntries={['/404']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText('NotFound')).toBeInTheDocument();
  });

  it('should render UNAUTHORIZED on "/unauthorized" route', () => {
    render(
      <MemoryRouter initialEntries={['/unauthorized']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText('UNAUTHORIZED')).toBeInTheDocument();
  });
  
  it('should render CatalogPage on "/catalog/:catalogId" route', () => {
    render(
      <MemoryRouter initialEntries={['/catalog/123']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText('CatalogPage')).toBeInTheDocument();
  });

  it('should render CatalogPage on "/" route', () => {
    render(
      <MemoryRouter initialEntries={['/']}>
        <App />
      </MemoryRouter>
    );
  
    expect(screen.getByText('CatalogPage')).toBeInTheDocument();
  });
});
