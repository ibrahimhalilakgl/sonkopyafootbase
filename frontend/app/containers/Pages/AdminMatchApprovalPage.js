import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  Button,
  Box,
  Grid,
  Alert,
  CircularProgress,
  Stack,
  Card,
  CardContent,
  Chip,
  Divider,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import { adminAPI } from 'utils/api';
import PapperBlock from 'dan-components/PapperBlock/PapperBlock';
import AuthService from 'utils/authService';
import { authAPI } from 'utils/api';

const authService = new AuthService(authAPI);

function AdminMatchApprovalPage() {
  const navigate = useNavigate();
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState({});
  const [error, setError] = useState(null);

  useEffect(() => {
    // Rol kontrolü
    const role = authService.getUserRole();
    if (role !== 'ADMIN') {
      navigate('/app');
      return;
    }
    loadPendingMatches();
  }, [navigate]);

  const loadPendingMatches = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await adminAPI.getPendingMatches();
      console.log('Onay bekleyen maçlar:', data);
      setMatches(Array.isArray(data) ? data : []);
      if (!Array.isArray(data) || data.length === 0) {
        console.log('Onay bekleyen maç bulunamadı veya boş liste döndü');
      }
    } catch (err) {
      console.error('Onay bekleyen maçlar yüklenemedi:', err);
      const errorMsg = err.data?.hata || err.data?.message || err.message || 'Onay bekleyen maçlar yüklenemedi. Lütfen tekrar deneyin.';
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (matchId) => {
    try {
      setProcessing((prev) => ({ ...prev, [matchId]: 'approving' }));
      setError(null);
      
      await adminAPI.approveMatch(matchId);
      
      // Başarılı onay sonrası listeden çıkar
      setMatches((prev) => prev.filter((m) => m.id !== matchId));
    } catch (err) {
      setError(err.message || 'Maç onaylanırken bir hata oluştu');
    } finally {
      setProcessing((prev) => {
        const newState = { ...prev };
        delete newState[matchId];
        return newState;
      });
    }
  };

  const handleReject = async (matchId) => {
    try {
      setProcessing((prev) => ({ ...prev, [matchId]: 'rejecting' }));
      setError(null);
      
      await adminAPI.rejectMatch(matchId);
      
      // Reddedilen maçı listeden çıkar
      setMatches((prev) => prev.filter((m) => m.id !== matchId));
    } catch (err) {
      setError(err.message || 'Maç reddedilirken bir hata oluştu');
    } finally {
      setProcessing((prev) => {
        const newState = { ...prev };
        delete newState[matchId];
        return newState;
      });
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const formatTime = (timeString) => {
    if (!timeString) return '-';
    return timeString.substring(0, 5); // HH:mm formatı
  };

  const getTeamName = (team) => {
    if (!team) return 'Bilinmiyor';
    return team.ad || team.name || 'Bilinmiyor';
  };

  if (loading) {
    return (
      <Container>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg">
      <PapperBlock title="Maç Onayları" icon="ion-ios-checkmark-circle" desc="Editörlerin eklediği maçları onaylayın veya reddedin">
        <Stack spacing={3}>
          {error && (
            <Alert severity="error" onClose={() => setError(null)}>
              {error}
            </Alert>
          )}

          {matches.length === 0 && !loading && (
            <Alert severity="info">
              Onay bekleyen maç bulunmamaktadır.
            </Alert>
          )}

          <Grid container spacing={3}>
            {matches.map((match) => {
              const isProcessing = processing[match.id];
              const evSahibiTakim = match.evSahibiTakim || (match.macTakimlari && match.macTakimlari.find((mt) => mt.evSahibi)?.takim);
              const deplasmanTakim = match.deplasmanTakim || (match.macTakimlari && match.macTakimlari.find((mt) => !mt.evSahibi)?.takim);

              return (
                <Grid item xs={12} key={match.id}>
                  <Card elevation={3}>
                    <CardContent>
                      <Stack spacing={2}>
                        <Stack direction="row" spacing={2} alignItems="center" justifyContent="space-between">
                          <Stack direction="row" spacing={2} alignItems="center">
                            <SportsSoccerIcon color="primary" />
                            <Typography variant="h6" fontWeight="bold">
                              {getTeamName(evSahibiTakim)} vs {getTeamName(deplasmanTakim)}
                            </Typography>
                          </Stack>
                          <Chip
                            label="Onay Bekliyor"
                            color="warning"
                            size="small"
                          />
                        </Stack>

                        <Divider />

                        <Grid container spacing={2}>
                          <Grid item xs={12} md={6}>
                            <Stack spacing={1}>
                              <Stack direction="row" spacing={1} alignItems="center">
                                <CalendarMonthIcon fontSize="small" color="action" />
                                <Typography variant="body2" color="text.secondary">
                                  Tarih: {formatDate(match.tarih)}
                                </Typography>
                              </Stack>
                              <Stack direction="row" spacing={1} alignItems="center">
                                <AccessTimeIcon fontSize="small" color="action" />
                                <Typography variant="body2" color="text.secondary">
                                  Saat: {formatTime(match.saat)}
                                </Typography>
                              </Stack>
                            </Stack>
                          </Grid>

                          <Grid item xs={12} md={6}>
                            <Stack direction="row" spacing={2} justifyContent="flex-end" sx={{ height: '100%' }} alignItems="center">
                              <Button
                                variant="outlined"
                                color="error"
                                startIcon={<CancelIcon />}
                                onClick={() => handleReject(match.id)}
                                disabled={!!isProcessing}
                              >
                                {isProcessing === 'rejecting' ? 'Reddediliyor...' : 'Reddet'}
                              </Button>
                              <Button
                                variant="contained"
                                color="success"
                                startIcon={<CheckCircleIcon />}
                                onClick={() => handleApprove(match.id)}
                                disabled={!!isProcessing}
                              >
                                {isProcessing === 'approving' ? 'Onaylanıyor...' : 'Onayla'}
                              </Button>
                            </Stack>
                          </Grid>
                        </Grid>
                      </Stack>
                    </CardContent>
                  </Card>
                </Grid>
              );
            })}
          </Grid>
        </Stack>
      </PapperBlock>
    </Container>
  );
}

export default AdminMatchApprovalPage;

