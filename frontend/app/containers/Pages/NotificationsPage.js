import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  Chip,
  Button,
  Box,
  Divider,
  IconButton,
  Alert,
} from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import CommentIcon from '@mui/icons-material/Comment';
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import PapperBlock from 'dan-components/PapperBlock/PapperBlock';
import { notificationsAPI } from 'utils/api';

/**
 * Bildirimler Sayfası
 * 
 * Kullanıcının tüm bildirimlerini listeler
 */
function NotificationsPage() {
  const navigate = useNavigate();
  const [bildirimler, setBildirimler] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadBildirimler();
  }, []);

  const loadBildirimler = async () => {
    try {
      setLoading(true);
      const data = await notificationsAPI.getAll();
      setBildirimler(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('Bildirimler yüklenemedi:', err);
      setError('Bildirimler yüklenirken bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const handleBildirimClick = async (bildirim) => {
    try {
      if (!bildirim.okundu) {
        await notificationsAPI.markAsRead(bildirim.id);
      }
      
      if (bildirim.hedefUrl) {
        navigate(bildirim.hedefUrl);
      }
      
      loadBildirimler();
    } catch (err) {
      console.error('Bildirim işlenemedi:', err);
    }
  };

  const handleTumunuOkundu = async () => {
    try {
      await notificationsAPI.markAllAsRead();
      loadBildirimler();
    } catch (err) {
      console.error('Tümü okundu işaretlenemedi:', err);
    }
  };

  const handleSil = async (id, e) => {
    e.stopPropagation();
    try {
      await notificationsAPI.deleteNotification(id);
      loadBildirimler();
    } catch (err) {
      console.error('Bildirim silinemedi:', err);
    }
  };

  const getBildirimIcon = (tip) => {
    switch (tip) {
      case 'MAC_EKLENDI':
      case 'MAC_ONAYLANDI':
      case 'MAC_BASLADI':
      case 'MAC_BITTI':
      case 'GOL_ATILDI':
        return <SportsSoccerIcon />;
      case 'MAC_REDDEDILDI':
        return <CancelIcon color="error" />;
      case 'YENI_YORUM':
        return <CommentIcon />;
      default:
        return <NotificationsIcon />;
    }
  };

  const getBildirimColor = (tip) => {
    switch (tip) {
      case 'MAC_ONAYLANDI':
        return 'success';
      case 'MAC_REDDEDILDI':
        return 'error';
      case 'MAC_EKLENDI':
        return 'warning';
      case 'GOL_ATILDI':
        return 'primary';
      default:
        return 'info';
    }
  };

  const okunmamisSayi = bildirimler.filter(b => !b.okundu).length;

  return (
    <div>
      <PapperBlock title="Bildirimler" icon="ion-ios-notifications-outline" desc="Tüm bildirimleriniz">
        <Container maxWidth="md">
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="body2" color="text.secondary">
              {okunmamisSayi > 0 ? `${okunmamisSayi} okunmamış bildirim` : 'Tüm bildirimler okundu'}
            </Typography>
            {okunmamisSayi > 0 && (
              <Button size="small" onClick={handleTumunuOkundu}>
                Tümünü Okundu İşaretle
              </Button>
            )}
          </Box>

          {loading ? (
            <Typography>Yükleniyor...</Typography>
          ) : bildirimler.length === 0 ? (
            <Paper sx={{ p: 3, textAlign: 'center' }}>
              <NotificationsIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
              <Typography color="text.secondary">
                Henüz bildiriminiz yok
              </Typography>
            </Paper>
          ) : (
            <List>
              {bildirimler.map((bildirim, index) => (
                <React.Fragment key={bildirim.id}>
                  <ListItem
                    button
                    onClick={() => handleBildirimClick(bildirim)}
                    sx={{
                      backgroundColor: bildirim.okundu ? 'transparent' : 'action.hover',
                      '&:hover': {
                        backgroundColor: 'action.selected',
                      },
                      borderRadius: 1,
                      mb: 1,
                    }}
                  >
                    <ListItemAvatar>
                      <Avatar sx={{ bgcolor: bildirim.okundu ? 'grey.400' : 'primary.main' }}>
                        {getBildirimIcon(bildirim.bildirimTipi)}
                      </Avatar>
                    </ListItemAvatar>
                    <ListItemText
                      primary={
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <Typography
                            variant="body1"
                            fontWeight={bildirim.okundu ? 'normal' : 'bold'}
                          >
                            {bildirim.baslik}
                          </Typography>
                          {!bildirim.okundu && (
                            <Box
                              sx={{
                                width: 8,
                                height: 8,
                                borderRadius: '50%',
                                backgroundColor: 'primary.main',
                              }}
                            />
                          )}
                        </Box>
                      }
                      secondary={
                        <>
                          <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
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
                              sx={{ mt: 1 }}
                            />
                          )}
                        </>
                      }
                    />
                    <IconButton
                      edge="end"
                      aria-label="delete"
                      onClick={(e) => handleSil(bildirim.id, e)}
                    >
                      <DeleteIcon />
                    </IconButton>
                  </ListItem>
                  {index < bildirimler.length - 1 && <Divider variant="inset" component="li" />}
                </React.Fragment>
              ))}
            </List>
          )}
        </Container>
      </PapperBlock>
    </div>
  );
}

export default NotificationsPage;

