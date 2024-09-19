import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getInstitutions } from 'app/entities/institution/institution.reducer';
import { createEntity, getEntity, reset, updateEntity } from './institution.reducer';

export const InstitutionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const institutions = useAppSelector(state => state.institution.entities);
  const institutionEntity = useAppSelector(state => state.institution.entity);
  const loading = useAppSelector(state => state.institution.loading);
  const updating = useAppSelector(state => state.institution.updating);
  const updateSuccess = useAppSelector(state => state.institution.updateSuccess);

  const handleClose = () => {
    navigate(`/institution${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getInstitutions({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...institutionEntity,
      ...values,
      parentInstitution: institutions.find(it => it.id.toString() === values.parentInstitution?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...institutionEntity,
          parentInstitution: institutionEntity?.parentInstitution?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApiApp.institution.home.createOrEditLabel" data-cy="InstitutionCreateUpdateHeading">
            Create or edit a Institution
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="institution-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Institution Name"
                id="institution-institutionName"
                name="institutionName"
                data-cy="institutionName"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                id="institution-parentInstitution"
                name="parentInstitution"
                data-cy="parentInstitution"
                label="Parent Institution"
                type="select"
              >
                <option value="" key="0" />
                {institutions
                  ? institutions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.institutionName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/institution" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default InstitutionUpdate;
