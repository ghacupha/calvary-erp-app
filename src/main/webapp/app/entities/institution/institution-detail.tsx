import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './institution.reducer';

export const InstitutionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const institutionEntity = useAppSelector(state => state.institution.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="institutionDetailsHeading">Institution</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{institutionEntity.id}</dd>
          <dt>
            <span id="institutionName">Institution Name</span>
          </dt>
          <dd>{institutionEntity.institutionName}</dd>
          <dt>Parent Institution</dt>
          <dd>{institutionEntity.parentInstitution ? institutionEntity.parentInstitution.institutionName : ''}</dd>
        </dl>
        <Button tag={Link} to="/institution" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/institution/${institutionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default InstitutionDetail;
