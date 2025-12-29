import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  Grid,
  Button,
  TextField,
  Alert,
  Box,
  Card,
  CardContent,
  Chip,
  Stack,
  Divider,
  IconButton,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemText,
  CircularProgress,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import UndoIcon from '@mui/icons-material/Undo';
import HistoryIcon from '@mui/icons-material/History';
import FlagIcon from '@mui/icons-material/Flag';
import SaveIcon from '@mui/icons-material/Save';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import PapperBlock from 'dan-components/PapperBlock/PapperBlock';
import AuthService from 'utils/authService';
import { authAPI, adminAPI, matchesAPI } from 'utils/api';

const authService = new AuthService(authAPI);

/**
 * Admin Maç Skor Yönetimi Sayfası (Command Pattern)
 * 
 * Bu sayfa Command Pattern kullanarak:
 * - Onaylanmış maçlara skor girişi yapar
 * - Maçları sonlandırır
 * - İşlemleri geri alabilir (UNDO) ⭐
 * - İşlem geçmişini gösterir
 */
function AdminMatchScoreManagementPage() {
  const navigate = useNavigate();
  
  // State
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [processing, setProcessing] = useState(false);
  
  // Skor girişi modal
  const [scoreModal, setScoreModal] = useState(false);
  const [selectedMatch, setSelectedMatch] = useState(null);
  const [homeScore, setHomeScore] = useState(0);
  const [awayScore, setAwayScore] = useState(0);
  
  // İşlem geçmişi modal
  const [historyModal, setHistoryModal] = useState(false);
  const [commandHistory, setCommandHistory] = useState([]);

  useEffect(() => {
    const role = authService.getUserRole();
    if (role !== 'ADMIN') {
      navigate('/app');
      return;
    }
    loadApprovedMatches();
    loadCommandHistory();
  }, [navigate]);

  /**
   * Onaylanmış maçları yükle
   */
  const loadApprovedMatches = async () => {
    try {
      setLoading(true);
      // Tüm maçları getir (YAYINDA olanlar)
      const allMatches = await matchesAPI.list();
      console.log('📊 Admin - Gelen maçlar:', allMatches);
      console.log('📊 Maç sayısı:', allMatches?.length);
      
      if (Array.isArray(allMatches)) {
        allMatches.forEach(m => {
          console.log(`Maç ID ${m.id}: durum=${m.durum}, onayDurumu=${m.onayDurumu}, evSahibi=${m.evSahibiTakim?.ad}, deplasman=${m.deplasmanTakim?.ad}`);
        });
      }
      
      // YAYINDA filtrelemesi: durum veya onayDurumu kontrolü
      const yayindaMaclar = Array.isArray(allMatches) 
        ? allMatches.filter(m => {
            const isYayinda = m.durum === 'YAYINDA' || m.onayDurumu === 'YAYINDA';
            console.log(`Maç ID ${m.id} filtreleme: ${isYayinda ? '✅ YAYINDA' : '❌ Değil'}`);
            return isYayinda;
          })
        : [];
      
      console.log('✅ Filtrelenmiş YAYINDA maç sayısı:', yayindaMaclar.length);
      setMatches(yayindaMaclar);
      setError(null);
    } catch (err) {
      console.error('❌ Maçlar yüklenemedi:', err);
      setError('Maçlar yüklenirken bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  /**
   * İşlem geçmişini yükle
   */
  const loadCommandHistory = async () => {
    try {
      const response = await adminAPI.getCommandHistory();
      setCommandHistory(response.gecmis || []);
    } catch (err) {
      console.error('İşlem geçmişi yüklenemedi:', err);
    }
  };

  /**
   * Skor girişi modal aç
   */
  const openScoreModal = (match) => {
    setSelectedMatch(match);
    setHomeScore(match.evSahibiSkor || 0);
    setAwayScore(match.deplasmanSkor || 0);
    setScoreModal(true);
    setError(null);
    setSuccess(null);
  };

  /**
   * Skor girişi yap (Command Pattern)
   */
  const handleScoreSubmit = async () => {
    if (!selectedMatch) return;
    
    try {
      setProcessing(true);
      setError(null);
      
      const payload = {
        macId: selectedMatch.id,
        evSahibiSkor: parseInt(homeScore, 10),
        deplasmanSkor: parseInt(awayScore, 10),
        aciklama: `Skor güncellendi: ${homeScore}-${awayScore}`,
      };
      
      const result = await adminAPI.updateMatchScore(payload);
      
      if (result.basarili) {
        setSuccess(`✅ Skor başarıyla güncellendi! (${homeScore}-${awayScore})`);
        setScoreModal(false);
        loadApprovedMatches();
        loadCommandHistory();
      } else {
        setError(result.mesaj || 'Skor güncellenemedi');
      }
    } catch (err) {
      console.error('Skor güncelleme hatası:', err);
      setError(err.message || 'Skor güncellenirken bir hata oluştu');
    } finally {
      setProcessing(false);
    }
  };

  /**
   * Maçı sonlandır (Command Pattern)
   */
  const handleFinishMatch = async () => {
    if (!selectedMatch) return;
    
    const confirmed = window.confirm(
      `${selectedMatch.evSahibiTakim?.ad || 'Ev Sahibi'} ${homeScore} - ${awayScore} ${selectedMatch.deplasmanTakim?.ad || 'Deplasman'}\n\nMaçı sonlandırmak istediğinize emin misiniz?`
    );
    
    if (!confirmed) return;
    
    try {
      setProcessing(true);
      setError(null);
      
      const payload = {
        macId: selectedMatch.id,
        evSahibiSkor: parseInt(homeScore, 10),
        deplasmanSkor: parseInt(awayScore, 10),
        durum: 'BITTI',
        aciklama: 'Maç sonlandırıldı',
      };
      
      const result = await adminAPI.finishMatch(payload);
      
      if (result.basarili) {
        setSuccess(`🏁 Maç başarıyla sonlandırıldı! Sonuç: ${homeScore}-${awayScore} (${result.sonuc})`);
        setScoreModal(false);
        loadApprovedMatches();
        loadCommandHistory();
      } else {
        setError(result.mesaj || 'Maç sonlandırılamadı');
      }
    } catch (err) {
      console.error('Maç sonlandırma hatası:', err);
      setError(err.message || 'Maç sonlandırılırken bir hata oluştu');
    } finally {
      setProcessing(false);
    }
  };

  /**
   * Son işlemi geri al (UNDO - Command Pattern)
   */
  const handleUndo = async () => {
    const confirmed = window.confirm('Son işlemi geri almak istediğinize emin misiniz?');
    if (!confirmed) return;
    
    try {
      setProcessing(true);
      setError(null);
      
      const result = await adminAPI.undoLastCommand();
      
      if (result.basarili) {
        setSuccess(`🔄 İşlem geri alındı! (${result.islemTipi})`);
        loadApprovedMatches();
        loadCommandHistory();
      } else {
        setError(result.mesaj || 'İşlem geri alınamadı');
      }
    } catch (err) {
      console.error('Undo hatası:', err);
      setError(err.message || 'İşlem geri alınırken bir hata oluştu');
    } finally {
      setProcessing(false);
    }
  };

  /**
   * Maç durumu rengini belirle
   */
  const getStatusColor = (match) => {
    if (match.durum === 'BITTI') return 'success';
    if (match.durum === 'DEVAM_EDIYOR') return 'warning';
    return 'primary';
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <PapperBlock
        title="Maç Skor Yönetimi (Command Pattern)"
        icon="ios-football"
        desc="Onaylanmış maçlara skor girin, maçları sonlandırın ve işlemleri geri alın! ⚡"
      >
        {/* Üst Butonlar */}
        <Box mb={3}>
          <Stack direction="row" spacing={2}>
            <Tooltip title="Son işlemi geri al (UNDO)">
              <Button
                variant="outlined"
                color="warning"
                startIcon={<UndoIcon />}
                onClick={handleUndo}
                disabled={processing || commandHistory.length === 0}
              >
                Geri Al (Undo)
              </Button>
            </Tooltip>
            
            <Tooltip title="İşlem geçmişini görüntüle">
              <Button
                variant="outlined"
                color="info"
                startIcon={<HistoryIcon />}
                onClick={() => setHistoryModal(true)}
              >
                İşlem Geçmişi ({commandHistory.length})
              </Button>
            </Tooltip>
          </Stack>
        </Box>

        {/* Mesajlar */}
        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}
        
        {success && (
          <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
            {success}
          </Alert>
        )}

        {/* Maç Listesi */}
        <Grid container spacing={3}>
          {matches.length === 0 ? (
            <Grid item xs={12}>
              <Paper sx={{ p: 3, textAlign: 'center' }}>
                <Typography color="text.secondary">
                  Henüz onaylanmış maç bulunmuyor
                </Typography>
              </Paper>
            </Grid>
          ) : (
            matches.map((match) => (
              <Grid item xs={12} md={6} key={match.id}>
                <Card>
                  <CardContent>
                    <Stack spacing={2}>
                      {/* Maç Bilgisi */}
                      <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Chip
                          label={match.durum || 'PLANLI'}
                          color={getStatusColor(match)}
                          size="small"
                        />
                        <Typography variant="caption" color="text.secondary">
                          <CalendarMonthIcon sx={{ fontSize: 14, mr: 0.5, verticalAlign: 'middle' }} />
                          {match.tarih}
                        </Typography>
                      </Box>

                      {/* Takımlar ve Skor */}
                      <Box>
                        <Stack direction="row" spacing={2} alignItems="center" justifyContent="space-between">
                          <Box flex={1} textAlign="center">
                            <Typography variant="body1" fontWeight="bold">
                              {match.evSahibiTakim?.ad || 'Ev Sahibi'}
                            </Typography>
                          </Box>
                          
                          <Box textAlign="center">
                            <Typography variant="h4" fontWeight="bold" color="primary">
                              {match.evSahibiSkor || 0} - {match.deplasmanSkor || 0}
                            </Typography>
                          </Box>
                          
                          <Box flex={1} textAlign="center">
                            <Typography variant="body1" fontWeight="bold">
                              {match.deplasmanTakim?.ad || 'Deplasman'}
                            </Typography>
                          </Box>
                        </Stack>
                      </Box>

                      <Divider />

                      {/* Aksiyonlar */}
                      <Stack direction="row" spacing={1}>
                        <Button
                          fullWidth
                          variant="contained"
                          color="primary"
                          startIcon={<SportsSoccerIcon />}
                          onClick={() => openScoreModal(match)}
                          disabled={match.durum === 'BITTI'}
                        >
                          Skor Gir
                        </Button>
                        
                        {match.durum !== 'BITTI' && (
                          <Tooltip title="Maçı sonlandır">
                            <IconButton
                              color="success"
                              onClick={() => openScoreModal(match)}
                            >
                              <FlagIcon />
                            </IconButton>
                          </Tooltip>
                        )}
                      </Stack>
                    </Stack>
                  </CardContent>
                </Card>
              </Grid>
            ))
          )}
        </Grid>
      </PapperBlock>

      {/* Skor Girişi Modal */}
      <Dialog open={scoreModal} onClose={() => !processing && setScoreModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          <Box display="flex" alignItems="center" gap={1}>
            <SportsSoccerIcon color="primary" />
            Skor Güncelle - Command Pattern
          </Box>
        </DialogTitle>
        <DialogContent>
          {selectedMatch && (
            <Box sx={{ pt: 2 }}>
              <Grid container spacing={3}>
                {/* Ev Sahibi */}
                <Grid item xs={12} sm={5}>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Ev Sahibi
                  </Typography>
                  <Typography variant="h6" gutterBottom>
                    {selectedMatch.evSahibiTakim?.ad || 'Ev Sahibi'}
                  </Typography>
                  <TextField
                    type="number"
                    label="Skor"
                    value={homeScore}
                    onChange={(e) => setHomeScore(Math.max(0, parseInt(e.target.value, 10) || 0))}
                    fullWidth
                    inputProps={{ min: 0 }}
                  />
                </Grid>

                {/* VS */}
                <Grid item xs={12} sm={2} display="flex" alignItems="center" justifyContent="center">
                  <Typography variant="h5" color="text.secondary">
                    VS
                  </Typography>
                </Grid>

                {/* Deplasman */}
                <Grid item xs={12} sm={5}>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Deplasman
                  </Typography>
                  <Typography variant="h6" gutterBottom>
                    {selectedMatch.deplasmanTakim?.ad || 'Deplasman'}
                  </Typography>
                  <TextField
                    type="number"
                    label="Skor"
                    value={awayScore}
                    onChange={(e) => setAwayScore(Math.max(0, parseInt(e.target.value, 10) || 0))}
                    fullWidth
                    inputProps={{ min: 0 }}
                  />
                </Grid>
              </Grid>

              {/* Uyarı */}
              <Alert severity="info" sx={{ mt: 2 }}>
                💡 <strong>Command Pattern:</strong> Bu işlemi geri alabilirsiniz! (Undo)
              </Alert>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setScoreModal(false)} disabled={processing}>
            İptal
          </Button>
          <Button
            onClick={handleScoreSubmit}
            variant="contained"
            color="primary"
            startIcon={processing ? <CircularProgress size={20} /> : <SaveIcon />}
            disabled={processing}
          >
            Skoru Kaydet
          </Button>
          <Button
            onClick={handleFinishMatch}
            variant="contained"
            color="success"
            startIcon={processing ? <CircularProgress size={20} /> : <FlagIcon />}
            disabled={processing || selectedMatch?.durum === 'BITTI'}
          >
            Maçı Sonlandır
          </Button>
        </DialogActions>
      </Dialog>

      {/* İşlem Geçmişi Modal */}
      <Dialog open={historyModal} onClose={() => setHistoryModal(false)} maxWidth="md" fullWidth>
        <DialogTitle>
          <Box display="flex" alignItems="center" gap={1}>
            <HistoryIcon color="primary" />
            İşlem Geçmişi (Command History)
          </Box>
        </DialogTitle>
        <DialogContent>
          {commandHistory.length === 0 ? (
            <Typography color="text.secondary" textAlign="center" sx={{ py: 3 }}>
              Henüz hiç işlem yapılmadı
            </Typography>
          ) : (
            <List>
              {commandHistory.map((cmd, index) => (
                <React.Fragment key={index}>
                  <ListItem>
                    <ListItemText
                      primary={cmd.aciklama}
                      secondary={
                        <Box>
                          <Typography variant="caption" component="span">
                            {cmd.tip} • {new Date(cmd.zaman).toLocaleString('tr-TR')}
                          </Typography>
                        </Box>
                      }
                    />
                    <Chip label={cmd.tip} size="small" color="primary" />
                  </ListItem>
                  {index < commandHistory.length - 1 && <Divider />}
                </React.Fragment>
              ))}
            </List>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setHistoryModal(false)}>Kapat</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}

export default AdminMatchScoreManagementPage;

