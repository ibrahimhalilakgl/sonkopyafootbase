import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Box,
  Grid,
  MenuItem,
  Alert,
  CircularProgress,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Divider,
  IconButton,
  Collapse,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Select,
  FormControl,
  InputLabel,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import PendingIcon from '@mui/icons-material/Pending';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import StopIcon from '@mui/icons-material/Stop';
import AddIcon from '@mui/icons-material/Add';
import { teamsAPI, playersAPI } from 'utils/api';
import { editorAPI } from 'utils/api';
import PapperBlock from 'dan-components/PapperBlock/PapperBlock';
import MatchActionsDialog from 'dan-components/Match/MatchActionsDialog';
import AuthService from 'utils/authService';
import { authAPI } from 'utils/api';

const authService = new AuthService(authAPI);

function EditorMatchAddPage() {
  const navigate = useNavigate();
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(false);
  const [teamsLoading, setTeamsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [myMatches, setMyMatches] = useState([]);
  const [matchesLoading, setMatchesLoading] = useState(false);
  
  // Dialog state
  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedMatch, setSelectedMatch] = useState(null);
  
  const [formData, setFormData] = useState({
    evSahibiTakimId: '',
    deplasmanTakimId: '',
    tarih: '',
    saat: '',
    hakemId: null,
  });

  useEffect(() => {
    // Rol kontrolü
    const role = authService.getUserRole();
    if (role !== 'EDITOR') {
      navigate('/app');
      return;
    }
    loadTeams();
    loadMyMatches();
  }, [navigate]);

  const loadTeams = async () => {
    try {
      setTeamsLoading(true);
      const data = await teamsAPI.list();
      setTeams(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Takımlar yüklenemedi:', error);
      setError('Takımlar yüklenemedi. Lütfen sayfayı yenileyin.');
    } finally {
      setTeamsLoading(false);
    }
  };

  const loadMyMatches = async () => {
    try {
      setMatchesLoading(true);
      const data = await editorAPI.getMyMatches();
      setMyMatches(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Maçlarınız yüklenemedi:', error);
    } finally {
      setMatchesLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);
    setLoading(true);

    try {
      // Form validasyonu
      if (!formData.evSahibiTakimId || !formData.deplasmanTakimId) {
        throw new Error('Lütfen ev sahibi ve deplasman takımını seçin');
      }

      if (formData.evSahibiTakimId === formData.deplasmanTakimId) {
        throw new Error('Ev sahibi ve deplasman takımı aynı olamaz');
      }

      if (!formData.tarih) {
        throw new Error('Lütfen maç tarihini girin');
      }

      if (!formData.saat) {
        throw new Error('Lütfen maç saatini girin');
      }

      // Maç verisi hazırla
      const matchData = {
        evSahibiTakim: { id: parseInt(formData.evSahibiTakimId) },
        deplasmanTakim: { id: parseInt(formData.deplasmanTakimId) },
        tarih: formData.tarih,
        saat: formData.saat,
        hakem: formData.hakemId ? { id: parseInt(formData.hakemId) } : null,
      };

      await editorAPI.createMatch(matchData);
      
      setSuccess(true);
      setFormData({
        evSahibiTakimId: '',
        deplasmanTakimId: '',
        tarih: '',
        saat: '',
        hakemId: null,
      });

      // Maçları yeniden yükle
      loadMyMatches();

      // 2 saniye sonra success mesajını kaldır
      setTimeout(() => {
        setSuccess(false);
      }, 3000);
    } catch (err) {
      // Backend'den gelen hata mesajını al
      const errorMsg = err.data?.hata || err.data?.message || err.message || 'Maç eklenirken bir hata oluştu';
      setError(errorMsg);
      console.error('Maç ekleme hatası:', err);
    } finally {
      setLoading(false);
    }
  };

  if (teamsLoading) {
    return (
      <Container>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <PapperBlock title="Maç Ekle" icon="ion-ios-football" desc="Yeni maç bilgilerini girin">
        <Paper elevation={3} sx={{ p: 4 }}>
          <Stack spacing={3}>
            {error && (
              <Alert severity="error" onClose={() => setError(null)}>
                {error}
              </Alert>
            )}

            {success && (
              <Alert severity="success">
                Maç başarıyla eklendi ve onay için admin'e gönderildi!
              </Alert>
            )}

            <form onSubmit={handleSubmit}>
              <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    select
                    label="Ev Sahibi Takım"
                    name="evSahibiTakimId"
                    value={formData.evSahibiTakimId}
                    onChange={handleChange}
                    required
                    variant="outlined"
                  >
                    {teams.map((team) => (
                      <MenuItem key={team.id} value={team.id}>
                        {team.ad || team.name}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>

                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    select
                    label="Deplasman Takımı"
                    name="deplasmanTakimId"
                    value={formData.deplasmanTakimId}
                    onChange={handleChange}
                    required
                    variant="outlined"
                  >
                    {teams.map((team) => (
                      <MenuItem key={team.id} value={team.id}>
                        {team.ad || team.name}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>

                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    type="date"
                    label="Maç Tarihi"
                    name="tarih"
                    value={formData.tarih}
                    onChange={handleChange}
                    required
                    InputLabelProps={{
                      shrink: true,
                    }}
                    variant="outlined"
                  />
                </Grid>

                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    type="time"
                    label="Maç Saati"
                    name="saat"
                    value={formData.saat}
                    onChange={handleChange}
                    required
                    InputLabelProps={{
                      shrink: true,
                    }}
                    variant="outlined"
                  />
                </Grid>

                <Grid item xs={12}>
                  <Stack direction="row" spacing={2} justifyContent="flex-end">
                    <Button
                      variant="outlined"
                      onClick={() => navigate('/app/matches')}
                      disabled={loading}
                    >
                      İptal
                    </Button>
                    <Button
                      type="submit"
                      variant="contained"
                      color="primary"
                      startIcon={<SportsSoccerIcon />}
                      disabled={loading}
                    >
                      {loading ? 'Ekleniyor...' : 'Maç Ekle'}
                    </Button>
                  </Stack>
                </Grid>
              </Grid>
            </form>
          </Stack>
        </Paper>
      </PapperBlock>

      {/* Eklediğim Maçlar Bölümü */}
      <Box mt={4}>
        <PapperBlock title="Eklediğim Maçlar" icon="ion-ios-list" desc="Onay bekleyen ve reddedilen maçlarınız">
          <Paper elevation={3} sx={{ p: 3 }}>
            {matchesLoading ? (
              <Box display="flex" justifyContent="center" py={4}>
                <CircularProgress />
              </Box>
            ) : myMatches.length === 0 ? (
              <Box textAlign="center" py={4}>
                <Typography variant="body1" color="textSecondary">
                  Henüz maç eklemediniz veya tüm maçlarınız onaylandı.
                </Typography>
              </Box>
            ) : (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell><strong>Ev Sahibi</strong></TableCell>
                      <TableCell><strong>Deplasman</strong></TableCell>
                      <TableCell><strong>Tarih</strong></TableCell>
                      <TableCell><strong>Saat</strong></TableCell>
                      <TableCell><strong>Durum</strong></TableCell>
                      <TableCell><strong>İşlemler</strong></TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {myMatches.map((match) => {
                      const now = new Date();
                      const matchDateTime = new Date(`${match.tarih}T${match.saat}`);
                      const canManage = now >= matchDateTime && match.onayDurumu === 'YAYINDA';
                      
                      return (
                        <TableRow key={match.id}>
                          <TableCell>
                            {match.evSahibiTakim?.ad || match.homeTeam?.name || '-'}
                          </TableCell>
                          <TableCell>
                            {match.deplasmanTakim?.ad || match.awayTeam?.name || '-'}
                          </TableCell>
                          <TableCell>
                            {match.tarih || match.date || '-'}
                          </TableCell>
                          <TableCell>
                            {match.saat || match.time || '-'}
                          </TableCell>
                          <TableCell>
                            {match.onayDurumu === 'ONAY_BEKLIYOR' && (
                              <Chip
                                icon={<PendingIcon />}
                                label="Onay Bekliyor"
                                color="warning"
                                size="small"
                              />
                            )}
                            {match.onayDurumu === 'REDDEDILDI' && (
                              <Chip
                                icon={<CancelIcon />}
                                label="Reddedildi"
                                color="error"
                                size="small"
                              />
                            )}
                            {match.onayDurumu === 'YAYINDA' && (
                              <Chip
                                icon={<CheckCircleIcon />}
                                label="Onaylandı"
                                color="success"
                                size="small"
                              />
                            )}
                          </TableCell>
                          <TableCell>
                            <Button
                              variant="contained"
                              color="primary"
                              size="small"
                              startIcon={<SportsSoccerIcon />}
                              onClick={() => {
                                setSelectedMatch(match);
                                setDialogOpen(true);
                              }}
                              disabled={!canManage}
                            >
                              {canManage ? 'Gol Gir / Yönet' : 'Maç Başlamadı'}
                            </Button>
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </Paper>
        </PapperBlock>
      </Box>
      
      {/* Match Actions Dialog */}
      <MatchActionsDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        match={selectedMatch}
        onSuccess={() => {
          loadMyMatches();
          setSuccess('İşlem başarılı!');
          setTimeout(() => setSuccess(false), 3000);
        }}
      />
    </Container>
  );
}

export default EditorMatchAddPage;

