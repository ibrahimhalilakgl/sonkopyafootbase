package com.footbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * Editor Yöneticileri entity sınıfı
 * Editor ve Admin ilişkisini temsil eder
 */
@Entity
@Table(name = "editor_yoneticileri")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@IdClass(EditorYoneticileriId.class)
public class EditorYoneticileri {

    /**
     * Editor kullanıcı ID (Primary Key)
     */
    @Id
    @Column(name = "editor_id", nullable = false)
    private Long editorId;

    /**
     * Admin kullanıcı ID (Part of composite primary key)
     */
    @Id
    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    /**
     * Editor kullanıcı
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id", insertable = false, updatable = false)
    private Kullanici editor;

    /**
     * Admin kullanıcı
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", insertable = false, updatable = false)
    private Kullanici admin;

    /**
     * Varsayılan constructor
     */
    public EditorYoneticileri() {
    }

    // Getter ve Setter metodları

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Kullanici getEditor() {
        return editor;
    }

    public void setEditor(Kullanici editor) {
        this.editor = editor;
    }

    public Kullanici getAdmin() {
        return admin;
    }

    public void setAdmin(Kullanici admin) {
        this.admin = admin;
    }
}

