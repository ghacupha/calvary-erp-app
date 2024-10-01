package io.github.erp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Institution.
 */
@Table("institution")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "institution")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Institution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Transient
    @JsonIgnoreProperties(value = { "institution" }, allowSetters = true)
    private Set<EntitySubscription> entitySubscriptions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Institution id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Institution name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<EntitySubscription> getEntitySubscriptions() {
        return this.entitySubscriptions;
    }

    public void setEntitySubscriptions(Set<EntitySubscription> entitySubscriptions) {
        if (this.entitySubscriptions != null) {
            this.entitySubscriptions.forEach(i -> i.setInstitution(null));
        }
        if (entitySubscriptions != null) {
            entitySubscriptions.forEach(i -> i.setInstitution(this));
        }
        this.entitySubscriptions = entitySubscriptions;
    }

    public Institution entitySubscriptions(Set<EntitySubscription> entitySubscriptions) {
        this.setEntitySubscriptions(entitySubscriptions);
        return this;
    }

    public Institution addEntitySubscription(EntitySubscription entitySubscription) {
        this.entitySubscriptions.add(entitySubscription);
        entitySubscription.setInstitution(this);
        return this;
    }

    public Institution removeEntitySubscription(EntitySubscription entitySubscription) {
        this.entitySubscriptions.remove(entitySubscription);
        entitySubscription.setInstitution(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Institution)) {
            return false;
        }
        return getId() != null && getId().equals(((Institution) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Institution{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
