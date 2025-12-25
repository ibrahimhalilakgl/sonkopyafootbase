import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  Grid,
  Button,
  TextField,
  MenuItem,
  Alert,
  Box,
  Card,
  CardContent,
  Chip,
  IconButton,
  Divider,
} from '@mui/material';
import { useParams, useNavigate } from 'react-router-dom';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import DeleteIcon from '@mui/icons-material/Delete';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import SaveIcon from '@mui/icons-material/Save';
import PapperBlock from 'dan-components/PapperBlock/PapperBlock';
import AuthService from 'utils/authService';
import { authAPI, editorAPI, matchesAPI, teamsAPI } from 'utils/api';

const authService = new AuthService(authAPI);

/**
 * Editör Maç Olay Ekleme Sayfası
 * 
 * Editörler bu sayfada:
 * - Gol ekleyebilir
 * - Kart ekleyebilir (sarı/kırmızı)
 * - Maç skorunu güncelleyebilir
 * - Maç durumunu değiştirebilir (Başlamadı, Devam Ediyor, Bitti)
 * 
 * Observer Pattern ile her olay eklendiğinde ilgili kişilere bildirim gider.
 */
function EditorMatchEventPage() {
  const { id } = useParams(); // Maç ID'si
  const navigate = useNavigate();
  
  // State
  const [mac, setMac] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  
  // Skor güncelleme
  const [evSahibiSkor, setEvSahibiSkor] = useState(0);
  const [deplasmanSkor, setDeplasmanSkor] = useState(0);
  
  // Olay ekleme
  const [olayTipi, setOlayTipi] = useState('GOL');
  const [oyuncuId, setOyuncuId] = useState('');
  const [dakika, setDakika] = useState('');
  
  // Oyuncular listesi
  const [oyuncular, setOyuncular] = useState([]);
  
  // Maç olayları
  const [macOlaylari, setMacOlaylari] = useState([]);
  
  /**
   * Maç bilgilerini yükle
   */
  useEffect(() => {
    const role = authService.getUserRole();
    if (role !== 'EDITOR') {
      navigate('/app');
      return;
    }
    loadMac();
  }, [id, navigate]);
  
  const loadMac = async () => {
    try {
      setLoading(true);
      
      // Maç bilgilerini getir
      const macData = await matchesAPI.get(id);
      setMac(macData);
      setEvSahibiSkor(macData.evSahibiSkor || 0);
      setDeplasmanSkor(macData.deplasmanSkor || 0);
      
      // Maç olaylarını getir
      loadMacOlaylari();
      
      // Oyuncuları getir (her iki takımdan)
      if (macData.evSahibiTakim && macData.deplasmanTakim) {
        await loadOyuncular(macData.evSahibiTakim.id, macData.deplasmanTakim.id);
      }
    } catch (err) {
      console.error('Maç yüklenirken hata:', err);
      setError('Maç bilgileri yüklenirken bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };
  
  const loadMacOlaylari = async () => {
    try {
      const data = await matchesAPI.getEvents(id);
      setMacOlaylari(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('Maç olayları yüklenemedi:', err);
    }
  };
  
  const loadOyuncular = async (evSahibiId, deplasmanId) => {
    try {
      // Her iki takımın oyuncularını getir
      const [evSahibiOyuncular, deplasmanOyuncular] = await Promise.all([
        teamsAPI.getPlayers(evSahibiId),
        teamsAPI.getPlayers(deplasmanId)
      ]);
      
      setOyuncular([...(evSahibiOyuncular || []), ...(deplasmanOyuncular || [])]);
    } catch (err) {
      console.error('Oyuncular yüklenemedi:', err);
    }
  };
  
  /**
   * Skor güncelle
   */
  const handleSkorGuncelle = async () => {
    try {
      await editorAPI.updateScore(id, {
        evSahibiSkor: parseInt(evSahibiSkor),
        deplasmanSkor: parseInt(deplasmanSkor)
      });
      
      setSuccess('Skor başarıyla güncellendi!');
      loadMac();
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      console.error('Skor güncellenirken hata:', err);
      setError(err.hata || 'Skor güncellenirken bir hata oluştu');
    }
  };
  
  /**
   * Olay ekle (Gol, Kart)
   */
  const handleOlayEkle = async () => {
    if (!oyuncuId || !dakika) {
      setError('Lütfen oyuncu ve dakika seçin');
      return;
    }
    
    try {
      await editorAPI.addMatchEvent(id, {
        oyuncu: { id: parseInt(oyuncuId) },
        olayTipi: olayTipi,
        dakika: parseInt(dakika)
      });
      
      setSuccess('Olay başarıyla eklendi!');
      loadMacOlaylari();
      
      // Formu temizle
      setOyuncuId('');
      setDakika('');
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      console.error('Olay eklenirken hata:', err);
      setError(err.hata || 'Olay eklenirken bir hata oluştu');
    }
  };
  
  if (loading) {
    return (
      <Container maxWidth="lg">
        <Typography>Yükleniyor...</Typography>
      </Container>
    );
  }
  
  if (!mac) {
    return (
      <Container maxWidth="lg">
        <Alert severity="error">Maç bulunamadı</Alert>
      </Container>
    );
  }
  
  return (
    <Container maxWidth="lg">
      <PapperBlock 
        title="Maç Olayları Yönetimi" 
        icon="ion-ios-football" 
        desc="Gol, kart ekleyin ve skoru güncelleyin"
      >
        {/* Maç Bilgileri */}
        <Card elevation={3} sx={{ mb: 3 }}>
          <CardContent>
            <Box display="flex" justifyContent="space-between" alignItems="center">
              <Box>
                <Typography variant="h5" gutterBottom>
                  {mac.evSahibiTakim?.ad || 'Bilinmiyor'} vs {mac.deplasmanTakim?.ad || 'Bilinmiyor'}
                </Typography>
                <Typography variant="h3" color="primary">
                  {mac.evSahibiSkor || 0} - {mac.deplasmanSkor || 0}
                </Typography>
              </Box>
              <Box>
                <Typography variant="body2" color="text.secondary">
                  {mac.tarih} {mac.saat}
                </Typography>
                <Chip label={mac.onayDurumu || 'YAYINDA'} color="success" />
              </Box>
            </Box>
          </CardContent>
        </Card>
        
        {error && (
          <Alert severity="error" onClose={() => setError(null)} sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        
        {success && (
          <Alert severity="success" onClose={() => setSuccess(null)} sx={{ mb: 2 }}>
            {success}
          </Alert>
        )}
        
        <Grid container spacing={3}>
          {/* Skor Güncelleme */}
          <Grid item xs={12} md={6}>
            <Paper elevation={3} sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                <SaveIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
                Skor Güncelle
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    label={mac.evSahibiTakim?.ad || 'Ev Sahibi'}
                    type="number"
                    value={evSahibiSkor}
                    onChange={(e) => setEvSahibiSkor(e.target.value)}
                    InputProps={{ inputProps: { min: 0 } }}
                  />
                </Grid>
                <Grid item xs={6}>
                  <TextField
                    fullWidth
                    label={mac.deplasmanTakim?.ad || 'Deplasman'}
                    type="number"
                    value={deplasmanSkor}
                    onChange={(e) => setDeplasmanSkor(e.target.value)}
                    InputProps={{ inputProps: { min: 0 } }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <Button
                    fullWidth
                    variant="contained"
                    color="primary"
                    onClick={handleSkorGuncelle}
                    startIcon={<SaveIcon />}
                  >
                    Skoru Kaydet
                  </Button>
                </Grid>
              </Grid>
            </Paper>
          </Grid>
          
          {/* Olay Ekleme */}
          <Grid item xs={12} md={6}>
            <Paper elevation={3} sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                <AddCircleIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
                Olay Ekle
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    select
                    fullWidth
                    label="Olay Tipi"
                    value={olayTipi}
                    onChange={(e) => setOlayTipi(e.target.value)}
                  >
                    <MenuItem value="GOL">⚽ Gol</MenuItem>
                    <MenuItem value="SARI_KART">🟨 Sarı Kart</MenuItem>
                    <MenuItem value="KIRMIZI_KART">🟥 Kırmızı Kart</MenuItem>
                  </TextField>
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    select
                    fullWidth
                    label="Oyuncu"
                    value={oyuncuId}
                    onChange={(e) => setOyuncuId(e.target.value)}
                  >
                    {oyuncular.map((oyuncu) => (
                      <MenuItem key={oyuncu.id} value={oyuncu.id}>
                        {oyuncu.ad} {oyuncu.soyad} ({oyuncu.takim?.ad})
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Dakika"
                    type="number"
                    value={dakika}
                    onChange={(e) => setDakika(e.target.value)}
                    InputProps={{ inputProps: { min: 1, max: 120 } }}
                    placeholder="Örn: 45"
                  />
                </Grid>
                <Grid item xs={12}>
                  <Button
                    fullWidth
                    variant="contained"
                    color="secondary"
                    onClick={handleOlayEkle}
                    startIcon={<AddCircleIcon />}
                  >
                    Olay Ekle
                  </Button>
                </Grid>
              </Grid>
            </Paper>
          </Grid>
          
          {/* Maç Olayları Listesi */}
          <Grid item xs={12}>
            <Paper elevation={3} sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                <SportsSoccerIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
                Maç Olayları ({macOlaylari.length})
              </Typography>
              <Divider sx={{ mb: 2 }} />
              
              {macOlaylari.length === 0 ? (
                <Typography color="text.secondary">Henüz olay eklenmedi</Typography>
              ) : (
                <Box>
                  {macOlaylari.map((olay, index) => (
                    <Box key={index} sx={{ mb: 2, p: 2, bgcolor: 'background.default', borderRadius: 1 }}>
                      <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Box>
                          <Typography variant="body1">
                            {olay.olayTuru === 'GOL' && '⚽'}
                            {olay.olayTuru === 'SARI_KART' && '🟨'}
                            {olay.olayTuru === 'KIRMIZI_KART' && '🟥'}
                            {' '}
                            {olay.oyuncu?.ad} {olay.oyuncu?.soyad}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            {olay.dakika}' - {olay.olayTuru}
                          </Typography>
                        </Box>
                        <Chip label={olay.oyuncu?.takim?.ad} size="small" />
                      </Box>
                    </Box>
                  ))}
                </Box>
              )}
            </Paper>
          </Grid>
        </Grid>
      </PapperBlock>
    </Container>
  );
}

export default EditorMatchEventPage;

