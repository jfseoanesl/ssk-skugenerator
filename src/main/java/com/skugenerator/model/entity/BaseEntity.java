package com.skugenerator.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad base que proporciona campos de auditoría para todas las entidades del sistema.
 *
 * Esta clase abstracta incluye:
 * - ID único autogenerado
 * - Campos de auditoría (creación y modificación)
 * - Soft delete con campo 'active'
 * - Versionado optimista con @Version
 *
 * @author SKU Generator Development Team
 * @version 1.0.0
 * @since 2024
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * Identificador único de la entidad.
     * Generado automáticamente usando estrategia IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * Fecha y hora de creación del registro.
     * Se establece automáticamente al crear la entidad.
     */
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Usuario que creó el registro.
     * Se establece automáticamente desde el contexto de seguridad.
     */
    @CreatedBy
    @Column(name = "created_by", length = 50, updatable = false)
    private String createdBy;

    /**
     * Fecha y hora de la última modificación del registro.
     * Se actualiza automáticamente cada vez que se modifica la entidad.
     */
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    /**
     * Usuario que realizó la última modificación.
     * Se actualiza automáticamente desde el contexto de seguridad.
     */
    @LastModifiedBy
    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    /**
     * Campo para soft delete.
     * true = activo (registro visible)
     * false = eliminado (registro oculto)
     */
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    /**
     * Campo de versión para control de concurrencia optimista.
     * Se incrementa automáticamente en cada actualización.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    /**
     * Método llamado antes de persistir la entidad.
     * Establece valores por defecto necesarios.
     */
    @PrePersist
    protected void onCreate() {
        if (active == null) {
            active = true;
        }
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }

    /**
     * Método llamado antes de actualizar la entidad.
     * Actualiza la fecha de modificación.
     */
    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

    /**
     * Método para realizar soft delete.
     * Marca el registro como inactivo en lugar de eliminarlo físicamente.
     */
    public void softDelete() {
        this.active = false;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * Método para restaurar un registro eliminado.
     * Marca el registro como activo nuevamente.
     */
    public void restore() {
        this.active = true;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * Verifica si el registro está activo.
     *
     * @return true si el registro está activo, false si está eliminado
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Verifica si es un nuevo registro (no persistido aún).
     *
     * @return true si es un nuevo registro, false si ya existe en BD
     */
    public boolean isNew() {
        return id == null;
    }

    // ===================================================================
    // MÉTODOS EQUALS, HASHCODE Y TOSTRING
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        // Si ambos objetos tienen ID, comparar por ID
        if (id != null && that.id != null) {
            return id.equals(that.id);
        }

        // Si uno o ambos no tienen ID, usar identidad de objeto
        return false;
    }

    @Override
    public int hashCode() {
        // Usar una constante para objetos no persistidos
        // y el ID para objetos persistidos
        return id != null ? id.hashCode() : 31;
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, active=%s, version=%d, createdDate=%s, createdBy='%s'}",
                getClass().getSimpleName(),
                id,
                active,
                version,
                createdDate,
                createdBy);
    }

    // ===================================================================
    // MÉTODOS DE UTILIDAD PARA AUDITORÍA
    // ===================================================================

    /**
     * Obtiene información de auditoría en formato legible.
     *
     * @return String con información de auditoría
     */
    public String getAuditInfo() {
        StringBuilder audit = new StringBuilder();
        audit.append("Creado: ").append(createdDate)
                .append(" por ").append(createdBy != null ? createdBy : "sistema");

        if (modifiedDate != null) {
            audit.append(" | Modificado: ").append(modifiedDate)
                    .append(" por ").append(modifiedBy != null ? modifiedBy : "sistema");
        }

        return audit.toString();
    }

    /**
     * Verifica si la entidad ha sido modificada después de su creación.
     *
     * @return true si ha sido modificada, false si es la versión original
     */
    public boolean isModified() {
        return modifiedDate != null && !modifiedDate.equals(createdDate);
    }

    /**
     * Calcula los días transcurridos desde la creación.
     *
     * @return número de días desde la creación
     */
    public long getDaysSinceCreation() {
        if (createdDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(createdDate.toLocalDate(),
                LocalDateTime.now().toLocalDate());
    }
}