import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import InstitutionalSubscription from './institutional-subscription';
import InstitutionalSubscriptionDetail from './institutional-subscription-detail';
import InstitutionalSubscriptionUpdate from './institutional-subscription-update';
import InstitutionalSubscriptionDeleteDialog from './institutional-subscription-delete-dialog';

const InstitutionalSubscriptionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<InstitutionalSubscription />} />
    <Route path="new" element={<InstitutionalSubscriptionUpdate />} />
    <Route path=":id">
      <Route index element={<InstitutionalSubscriptionDetail />} />
      <Route path="edit" element={<InstitutionalSubscriptionUpdate />} />
      <Route path="delete" element={<InstitutionalSubscriptionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InstitutionalSubscriptionRoutes;
