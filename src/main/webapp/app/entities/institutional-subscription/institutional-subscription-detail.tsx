import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './institutional-subscription.reducer';

export const InstitutionalSubscriptionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const institutionalSubscriptionEntity = useAppSelector(state => state.institutionalSubscription.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="institutionalSubscriptionDetailsHeading">Institutional Subscription</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{institutionalSubscriptionEntity.id}</dd>
          <dt>
            <span id="startDate">Start Date</span>
          </dt>
          <dd>
            {institutionalSubscriptionEntity.startDate ? (
              <TextFormat value={institutionalSubscriptionEntity.startDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="expiryDate">Expiry Date</span>
          </dt>
          <dd>
            {institutionalSubscriptionEntity.expiryDate ? (
              <TextFormat value={institutionalSubscriptionEntity.expiryDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="memberLimit">Member Limit</span>
          </dt>
          <dd>{institutionalSubscriptionEntity.memberLimit}</dd>
          <dt>Institution</dt>
          <dd>{institutionalSubscriptionEntity.institution ? institutionalSubscriptionEntity.institution.institutionName : ''}</dd>
        </dl>
        <Button tag={Link} to="/institutional-subscription" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/institutional-subscription/${institutionalSubscriptionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default InstitutionalSubscriptionDetail;
