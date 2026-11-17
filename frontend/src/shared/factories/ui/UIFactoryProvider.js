import PresidentUIFactory from './PresidentUIFactory';
import BranchAdminUIFactory from './BranchAdminUIFactory';

class UIFactoryProvider {
  static getFactory(role) {
    switch (role) {
      case 'ROLE_PRESIDENT':
      case 'PRESIDENT':
        return new PresidentUIFactory();

      case 'ROLE_BRANCH_ADMIN':
      case 'BRANCH_ADMIN':
        return new BranchAdminUIFactory();

      default:
        throw new Error(`Rol no soportado: ${role}`);
    }
  }
}

export default UIFactoryProvider;