package com.footbase.repository;

import com.footbase.entity.EditorYoneticileri;
import com.footbase.entity.EditorYoneticileriId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Editor Yöneticileri repository interface'i
 * Editor-Admin ilişkilerini yönetir
 */
@Repository
public interface EditorYoneticileriRepository extends JpaRepository<EditorYoneticileri, EditorYoneticileriId> {
    
    /**
     * Editor ID'sine göre admin'i bulur
     * @param editorId Editor ID'si
     * @return Editor-Admin ilişkisi
     */
    EditorYoneticileri findByEditorId(Long editorId);
    
    /**
     * Admin ID'sine göre tüm editor'leri bulur
     * @param adminId Admin ID'si
     * @return Admin'in editor'leri
     */
    List<EditorYoneticileri> findByAdminId(Long adminId);
    
    /**
     * Admin ID'sine göre editor'leri detaylı şekilde getirir
     * @param adminId Admin ID'si
     * @return Admin'in editor'leri (ilişkili entity'lerle)
     */
    @Query("SELECT DISTINCT ey FROM EditorYoneticileri ey " +
           "LEFT JOIN FETCH ey.editor " +
           "LEFT JOIN FETCH ey.admin " +
           "WHERE ey.adminId = :adminId")
    List<EditorYoneticileri> findByAdminIdWithDetails(Long adminId);
    
    /**
     * Editor ID'sine göre admin'i detaylı şekilde getirir
     * @param editorId Editor ID'si
     * @return Editor-Admin ilişkisi (ilişkili entity'lerle)
     */
    @Query("SELECT DISTINCT ey FROM EditorYoneticileri ey " +
           "LEFT JOIN FETCH ey.editor " +
           "LEFT JOIN FETCH ey.admin " +
           "WHERE ey.editorId = :editorId")
    EditorYoneticileri findByEditorIdWithDetails(Long editorId);
}

