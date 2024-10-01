import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'application-user',
    data: { pageTitle: 'ApplicationUsers' },
    loadChildren: () => import('./application-user/application-user.routes'),
  },
  {
    path: 'entity-subscription',
    data: { pageTitle: 'EntitySubscriptions' },
    loadChildren: () => import('./entity-subscription/entity-subscription.routes'),
  },
  {
    path: 'institution',
    data: { pageTitle: 'Institutions' },
    loadChildren: () => import('./institution/institution.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
