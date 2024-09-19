import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getInstitutions } from 'app/entities/institution/institution.reducer';
import { createEntity, getEntity, reset, updateEntity } from './institutional-subscription.reducer';

export const InstitutionalSubscriptionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const institutions = useAppSelector(state => state.institution.entities);
  const institutionalSubscriptionEntity = useAppSelector(state => state.institutionalSubscription.entity);
  const loading = useAppSelector(state => state.institutionalSubscription.loading);
  const updating = useAppSelector(state => state.institutionalSubscription.updating);
  const updateSuccess = useAppSelector(state => state.institutionalSubscription.updateSuccess);

  const handleClose = () => {
    navigate(`/institutional-subscription${location.search}`);
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
    if (values.memberLimit !== undefined && typeof values.memberLimit !== 'number') {
      values.memberLimit = Number(values.memberLimit);
    }

    const entity = {
      ...institutionalSubscriptionEntity,
      ...values,
      institution: institutions.find(it => it.id.toString() === values.institution?.toString()),
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
          ...institutionalSubscriptionEntity,
          institution: institutionalSubscriptionEntity?.institution?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="calvaryErpApiApp.institutionalSubscription.home.createOrEditLabel" data-cy="InstitutionalSubscriptionCreateUpdateHeading">
            Create or edit a Institutional Subscription
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="institutional-subscription-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Start Date"
                id="institutional-subscription-startDate"
                name="startDate"
                data-cy="startDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Expiry Date"
                id="institutional-subscription-expiryDate"
                name="expiryDate"
                data-cy="expiryDate"
                type="date"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Member Limit"
                id="institutional-subscription-memberLimit"
                name="memberLimit"
                data-cy="memberLimit"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <ValidatedField
                id="institutional-subscription-institution"
                name="institution"
                data-cy="institution"
                label="Institution"
                type="select"
                required
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
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/institutional-subscription" replace color="info">
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

export default InstitutionalSubscriptionUpdate;
