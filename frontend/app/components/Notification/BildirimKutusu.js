import React, { useState, useEffect } from 'react';
import {
  IconButton,
  Badge,
  Menu,
  MenuItem,
  Typography,
  Box,
  Divider,
  Button,
  CircularProgress,
  Chip,
} from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import CommentIcon from '@mui/icons-material/Comment';
import { useNavigate } from 'react-router-dom';
import { notificationsAPI } from 'utils/api';

/**
 * Bildirim Kutusu Komponenti
 * 
 * Observer Pattern ile oluşturulan bildirimleri gösterir.
 * Header'da bildirim ikonu olarak yerleştirilir.
 * 
 * Özellikler:
 * - Okunmamış bildirim sayısı badge'i
 * - Bildirim listesi dropdown
 * - Tek tıkla okundu işaretleme
 * - Hedef sayfaya yönlendirme
 * - Tümünü okundu işaretle
 * 
 * @author FootBase Takımı
 * @version 1.0
 */
function BildirimKutusu() {
  const navigate = useNavigate();
  
  // State tanımlamaları
  const [anchorEl, setAnchorEl] = useState(null); // Dropdown anchor
  const [bildirimler, setBildirimler] = useState([]); // Bildirim listesi
  const [okunmamisSayi, setOkunmamisSayi] = useState(0); // Badge sayısı
  const [loading, setLoading] = useState(false); // Yükleniyor durumu
  
  const open = Boolean(anchorEl);
  
  /**
     * Bildirimleri yükler
     * Son 10 bildirimi getirir
     */
  const loadBildirimler = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      if (!token) return;
      
      // API kullanarak bildirimleri getir
      const data = await notificationsAPI.getRecent(10);
      setBildirimler(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Bildirimler yüklenemedi:', error);
      setBildirimler([]);
    } finally {
      setLoading(false);
    }
  };
  
  /**
   * Okunmamış bildirim sayısını yükler
   */
  const loadOkunmamisSayi = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return;
      
      const data = await notificationsAPI.getUnreadCount();
      setOkunmamisSayi(data.count || 0);
    } catch (error) {
      console.error('Okunmamış sayı yüklenemedi:', error);
    }
  };
  
  /**
   * Component mount olduğunda ve her 30 saniyede bildirimleri yükle
   */
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) return;
    
    loadBildirimler();
    loadOkunmamisSayi();
    
    // Her 30 saniyede bir yenile
    const interval = setInterval(() => {
      loadOkunmamisSayi();
    }, 30000);
    
    return () => clearInterval(interval);
  }, []);
  
  /**
   * Bildirim ikonuna tıklandığında
   */
  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
    loadBildirimler(); // Dropdown açıldığında yenile
  };
  
  /**
   * Dropdown'ı kapat
   */
  const handleClose = () => {
    setAnchorEl(null);
  };
  
  /**
   * Bildirime tıklandığında
   * Bildirimi okundu işaretle ve hedef sayfaya yönlendir
   */
  const handleBildirimClick = async (bildirim) => {
    try {
      const token = localStorage.getItem('token');
      if (token && !bildirim.okundu) {
        // Okundu olarak işaretle
        await notificationsAPI.markAsRead(bildirim.id);
      }
      
      // Hedef sayfaya yönlendir
      if (bildirim.hedefUrl) {
        navigate(bildirim.hedefUrl);
      }
      
      handleClose();
      loadOkunmamisSayi(); // Sayıyı güncelle
    } catch (error) {
      console.error('Bildirim işlenemedi:', error);
    }
  };
  
  /**
   * Tümünü okundu işaretle
   */
  const handleTumunuOkundu = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return;
      
      await notificationsAPI.markAllAsRead();
      
      loadBildirimler();
      setOkunmamisSayi(0);
    } catch (error) {
      console.error('Tümü okundu işaretlenemedi:', error);
    }
  };
  
  /**
   * Bildirim tipine göre ikon döndür
   */
  const getBildirimIcon = (tip) => {
    switch (tip) {
      case 'MAC_EKLENDI':
      case 'MAC_ONAYLANDI':
      case 'MAC_BASLADI':
      case 'MAC_BITTI':
        return <SportsSoccerIcon fontSize="small" />;
      case 'MAC_REDDEDILDI':
        return <CancelIcon fontSize="small" color="error" />;
      case 'YENI_YORUM':
        return <CommentIcon fontSize="small" />;
      default:
        return <NotificationsIcon fontSize="small" />;
    }
  };
  
  /**
   * Bildirim tipine göre renk döndür
   */
  const getBildirimColor = (tip) => {
    switch (tip) {
      case 'MAC_ONAYLANDI':
        return 'success';
      case 'MAC_REDDEDILDI':
        return 'error';
      case 'MAC_EKLENDI':
        return 'warning';
      default:
        return 'info';
    }
  };
  
  // Kullanıcı giriş yapmamışsa gösterme
  const token = localStorage.getItem('token');
  if (!token) {
    return null;
  }
  
  return (
    <>
      <IconButton
        color="inherit"
        onClick={handleClick}
        aria-label="bildirimler"
      >
        <Badge badgeContent={okunmamisSayi} color="error">
          <NotificationsIcon />
        </Badge>
      </IconButton>
      
      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        PaperProps={{
          style: {
            maxHeight: 400,
            width: 360,
          },
        }}
      >
        {/* Başlık */}
        <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6">Bildirimler</Typography>
          {okunmamisSayi > 0 && (
            <Button size="small" onClick={handleTumunuOkundu}>
              Tümünü Okundu İşaretle
            </Button>
          )}
        </Box>
        
        <Divider />
        
        {/* Yükleniyor */}
        {loading && (
          <Box sx={{ p: 3, textAlign: 'center' }}>
            <CircularProgress size={24} />
          </Box>
        )}
        
        {/* Bildirim listesi */}
        {!loading && bildirimler.length === 0 && (
          <Box sx={{ p: 3, textAlign: 'center' }}>
            <Typography color="text.secondary">
              Henüz bildiriminiz yok
            </Typography>
          </Box>
        )}
        
        {!loading && bildirimler.map((bildirim) => (
          <MenuItem
            key={bildirim.id}
            onClick={() => handleBildirimClick(bildirim)}
            sx={{
              backgroundColor: bildirim.okundu ? 'transparent' : 'action.hover',
              '&:hover': {
                backgroundColor: 'action.selected',
              },
              whiteSpace: 'normal',
              py: 1.5,
            }}
          >
            <Box sx={{ display: 'flex', gap: 2, width: '100%' }}>
              {/* İkon */}
              <Box sx={{ mt: 0.5 }}>
                {getBildirimIcon(bildirim.bildirimTipi)}
              </Box>
              
              {/* İçerik */}
              <Box sx={{ flex: 1, minWidth: 0 }}>
                <Typography variant="body2" fontWeight={bildirim.okundu ? 'normal' : 'bold'}>
                  {bildirim.baslik}
                </Typography>
                <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5 }}>
                  {bildirim.icerik}
                </Typography>
                <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5 }}>
                  {new Date(bildirim.olusturmaZamani).toLocaleString('tr-TR')}
                </Typography>
                {bildirim.bildirimTipi && (
                  <Chip
                    label={bildirim.bildirimTipi.replace(/_/g, ' ')}
                    size="small"
                    color={getBildirimColor(bildirim.bildirimTipi)}
                    sx={{ mt: 0.5 }}
                  />
                )}
              </Box>
              
              {/* Okunmadı işareti */}
              {!bildirim.okundu && (
                <Box sx={{ width: 8, height: 8, borderRadius: '50%', backgroundColor: 'primary.main', mt: 1 }} />
              )}
            </Box>
          </MenuItem>
        ))}
        
        {/* Tümünü Gör */}
        {bildirimler.length > 0 && (
          <>
            <Divider />
            <Box sx={{ p: 1, textAlign: 'center' }}>
              <Button fullWidth onClick={() => { handleClose(); navigate('/app/notifications'); }}>
                Tüm Bildirimleri Gör
              </Button>
            </Box>
          </>
        )}
      </Menu>
    </>
  );
}

export default BildirimKutusu;

