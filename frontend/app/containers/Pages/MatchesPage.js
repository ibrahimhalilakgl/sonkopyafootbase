import React, { useState, useEffect, useMemo } from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  TextField,
  Box,
  Chip,
  Stack,
  Divider,
  Paper,
  Avatar,
} from '@mui/material';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import PlaceIcon from '@mui/icons-material/Place';
import StadiumIcon from '@mui/icons-material/Stadium';
import { matchesAPI } from 'utils/api';
import PapperBlock from 'dan-components/PapperBlock/PapperBlock';

const statusLabels = [
  { key: null, label: 'Tümü' },
  { key: 'PLANLI', label: 'Planlı' },
  { key: 'FINISHED', label: 'Bitti' },
  { key: 'BITTI', label: 'Bitti' },
  { key: 'SCHEDULED', label: 'Takvim' },
];

function MatchesPage() {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [error, setError] = useState(null);
  const [statusFilter, setStatusFilter] = useState(null);

  useEffect(() => {
    loadMatches();
  }, []);

  const loadMatches = async () => {
    try {
      setLoading(true);
      const data = await matchesAPI.list();
      setMatches(Array.isArray(data) ? data : []);
      setError(null);
    } catch (e) {
      console.error('Maçlar yüklenemedi:', e);
      setError('Maç listesi alınamadı. Lütfen daha sonra tekrar deneyin.');
    } finally {
      setLoading(false);
    }
  };

  const filteredMatches = useMemo(() => (
    matches.filter((match) => {
      if (!match) return false;
      const term = searchTerm.toLowerCase();
      const statusOk = !statusFilter || (match.status || match.durum || '').toUpperCase() === statusFilter;
      const homeTeamName = match.homeTeam || (match.evSahibiTakim && match.evSahibiTakim.ad ? match.evSahibiTakim.ad : '');
      const awayTeamName = match.awayTeam || (match.deplasmanTakim && match.deplasmanTakim.ad ? match.deplasmanTakim.ad : '');
      return statusOk && (
        (homeTeamName && homeTeamName.toLowerCase().includes(term)) ||
        (awayTeamName && awayTeamName.toLowerCase().includes(term))
      );
    })
  ), [matches, searchTerm, statusFilter]);

  // Gelecek ve geçmiş maçları ayır
  const { upcomingMatches, pastMatches } = useMemo(() => {
    const now = new Date();
    const upcoming = [];
    const past = [];

    filteredMatches.forEach((match) => {
      let kickoffDate = null;
      if (match.kickoffAt) {
        kickoffDate = new Date(match.kickoffAt);
      } else if (match.tarih && match.saat) {
        kickoffDate = new Date(`${match.tarih}T${match.saat}`);
      } else if (match.tarih) {
        kickoffDate = new Date(match.tarih);
      }

      if (kickoffDate && kickoffDate > now) {
        upcoming.push(match);
      } else {
        past.push(match);
      }
    });

    return { upcomingMatches: upcoming, pastMatches: past };
  }, [filteredMatches]);

  const hero = (
    <Paper
      elevation={3}
      sx={{
        p: { xs: 2, md: 4 },
        mb: 3,
        borderRadius: 3,
        background: 'linear-gradient(135deg, #0d2b45 0%, #14746f 100%)',
        color: '#fff',
      }}
    >
      <Stack spacing={2}>
        <Stack direction="row" spacing={1} alignItems="center">
          <StadiumIcon />
          <Typography variant="h5" fontWeight="bold">Haftanın Maçları</Typography>
        </Stack>
        <Typography variant="body1" sx={{ maxWidth: 720 }}>
          Program, canlı skor ve taraftar yorumları tek ekranda. Takımını ara, biten maçları incele,
          yaklaşan maçları kaçırma.
        </Typography>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
          <TextField
            fullWidth
            label="Takım veya maç ara"
            variant="outlined"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Örneğin: Galatasaray, Beşiktaş"
            InputProps={{ sx: { bgcolor: '#fff' } }}
          />
          <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
            {statusLabels.map((s) => (
              <Chip
                key={s.key || 'all'}
                label={s.label}
                color={statusFilter === s.key ? 'primary' : 'default'}
                onClick={() => setStatusFilter(s.key)}
              />
            ))}
          </Stack>
        </Stack>
      </Stack>
    </Paper>
  );

  return (
    <Container maxWidth="lg">
      <PapperBlock
        title="Maçlar"
        icon="ios-football"
        desc="Güncel maçları takip et, tahmin yap, yorumları oku."
      >
        {hero}

        {loading && (
          <Typography color="text.secondary">Yükleniyor...</Typography>
        )}

        {error && (
          <Typography color="error" mb={2}>{error}</Typography>
        )}

        {!loading && !error && (
          <Box>
            {/* Gelecek Maçlar Bölümü */}
            {upcomingMatches.length > 0 && (
              <Box mb={4}>
                <Box 
                  sx={{ 
                    mb: 3, 
                    p: 2, 
                    bgcolor: 'primary.main', 
                    color: 'white', 
                    borderRadius: 2,
                    boxShadow: 2
                  }}
                >
                  <Stack direction="row" spacing={1} alignItems="center">
                    <CalendarMonthIcon />
                    <Typography variant="h5" fontWeight="bold">
                      Yaklaşan Maçlar ({upcomingMatches.length})
                    </Typography>
                  </Stack>
                </Box>
                <Grid container spacing={3}>
                  {upcomingMatches.map((match) => {
                    const homeTeamName = match.homeTeam || (match.evSahibiTakim && match.evSahibiTakim.ad ? match.evSahibiTakim.ad : 'Bilinmiyor');
                    const awayTeamName = match.awayTeam || (match.deplasmanTakim && match.deplasmanTakim.ad ? match.deplasmanTakim.ad : 'Bilinmiyor');
                    const homeTeamLogo = match.homeTeamLogo || (match.evSahibiTakim && match.evSahibiTakim.logo ? match.evSahibiTakim.logo : null);
                    const awayTeamLogo = match.awayTeamLogo || (match.deplasmanTakim && match.deplasmanTakim.logo ? match.deplasmanTakim.logo : null);
                    const homeScore = match.homeScore != null ? match.homeScore : (match.evSahibiSkor != null ? match.evSahibiSkor : null);
                    const awayScore = match.awayScore != null ? match.awayScore : (match.deplasmanSkor != null ? match.deplasmanSkor : null);
                    const status = match.status || match.durum || 'PLANLI';
                    let kickoffDate = null;
                    if (match.kickoffAt) {
                      kickoffDate = new Date(match.kickoffAt);
                    } else if (match.tarih && match.saat) {
                      kickoffDate = new Date(`${match.tarih}T${match.saat}`);
                    } else if (match.tarih) {
                      kickoffDate = new Date(match.tarih);
                    }
                    return (
                      <Grid item xs={12} md={6} key={match.id}>
                        <Card elevation={3} sx={{ borderRadius: 2, border: '2px solid', borderColor: 'primary.light' }}>
                          <CardContent>
                            <Stack spacing={1.5}>
                              <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
                                <Stack direction="row" spacing={1} alignItems="center">
                                  <SportsSoccerIcon fontSize="small" color="primary" />
                                  <Typography variant="h6" fontWeight="bold">
                                    {homeTeamName} vs {awayTeamName}
                                  </Typography>
                                </Stack>
                                <Chip
                                  label={status}
                                  color="primary"
                                  size="small"
                                />
                              </Stack>

                              <Stack direction="row" spacing={2} alignItems="center" justifyContent="space-between">
                                <Stack direction="row" spacing={1} alignItems="center">
                                  <Avatar src={homeTeamLogo || undefined} alt={homeTeamName} />
                                  <Typography variant="subtitle1">{homeTeamName}</Typography>
                                </Stack>
                                <Typography variant="h6" fontWeight="bold">
                                  {homeScore != null ? homeScore : '-'} - {awayScore != null ? awayScore : '-'}
                                </Typography>
                                <Stack direction="row" spacing={1} alignItems="center">
                                  <Typography variant="subtitle1">{awayTeamName}</Typography>
                                  <Avatar src={awayTeamLogo || undefined} alt={awayTeamName} />
                                </Stack>
                              </Stack>

                              <Stack direction="row" spacing={2} alignItems="center" color="text.secondary">
                                {kickoffDate && (
                                  <Stack direction="row" spacing={0.5} alignItems="center">
                                    <CalendarMonthIcon fontSize="small" />
                                    <Typography variant="body2">
                                      {kickoffDate.toLocaleString('tr-TR')}
                                    </Typography>
                                  </Stack>
                                )}
                              </Stack>

                              <Divider />
                              <Box display="flex" justifyContent="space-between" alignItems="center">
                                <Typography variant="body2" color="text.secondary">
                                  Yorumlar ve detaylar için aç
                                </Typography>
                                <Button
                                  variant="contained"
                                  color="primary"
                                  href={`/app/matches/${match.id}`}
                                >
                                  Detaylara Git
                                </Button>
                              </Box>
                            </Stack>
                          </CardContent>
                        </Card>
                      </Grid>
                    );
                  })}
                </Grid>
              </Box>
            )}

            {/* Geçmiş Maçlar Bölümü */}
            {pastMatches.length > 0 && (
              <Box>
                <Box 
                  sx={{ 
                    mb: 3, 
                    p: 2, 
                    bgcolor: 'success.main', 
                    color: 'white', 
                    borderRadius: 2,
                    boxShadow: 2
                  }}
                >
                  <Stack direction="row" spacing={1} alignItems="center">
                    <SportsSoccerIcon />
                    <Typography variant="h5" fontWeight="bold">
                      Oynanan Maçlar ({pastMatches.length})
                    </Typography>
                  </Stack>
                </Box>
                <Grid container spacing={3}>
                  {pastMatches.map((match) => {
                    const homeTeamName = match.homeTeam || (match.evSahibiTakim && match.evSahibiTakim.ad ? match.evSahibiTakim.ad : 'Bilinmiyor');
                    const awayTeamName = match.awayTeam || (match.deplasmanTakim && match.deplasmanTakim.ad ? match.deplasmanTakim.ad : 'Bilinmiyor');
                    const homeTeamLogo = match.homeTeamLogo || (match.evSahibiTakim && match.evSahibiTakim.logo ? match.evSahibiTakim.logo : null);
                    const awayTeamLogo = match.awayTeamLogo || (match.deplasmanTakim && match.deplasmanTakim.logo ? match.deplasmanTakim.logo : null);
                    const homeScore = match.homeScore != null ? match.homeScore : (match.evSahibiSkor != null ? match.evSahibiSkor : null);
                    const awayScore = match.awayScore != null ? match.awayScore : (match.deplasmanSkor != null ? match.deplasmanSkor : null);
                    const status = match.status || match.durum || 'BİTTİ';
                    let kickoffDate = null;
                    if (match.kickoffAt) {
                      kickoffDate = new Date(match.kickoffAt);
                    } else if (match.tarih && match.saat) {
                      kickoffDate = new Date(`${match.tarih}T${match.saat}`);
                    } else if (match.tarih) {
                      kickoffDate = new Date(match.tarih);
                    }
                    return (
                      <Grid item xs={12} md={6} key={match.id}>
                        <Card elevation={3} sx={{ borderRadius: 2, opacity: 0.9 }}>
                          <CardContent>
                            <Stack spacing={1.5}>
                              <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
                                <Stack direction="row" spacing={1} alignItems="center">
                                  <SportsSoccerIcon fontSize="small" />
                                  <Typography variant="h6" fontWeight="bold">
                                    {homeTeamName} vs {awayTeamName}
                                  </Typography>
                                </Stack>
                                <Chip
                                  label={status}
                                  color="success"
                                  size="small"
                                />
                              </Stack>

                              <Stack direction="row" spacing={2} alignItems="center" justifyContent="space-between">
                                <Stack direction="row" spacing={1} alignItems="center">
                                  <Avatar src={homeTeamLogo || undefined} alt={homeTeamName} />
                                  <Typography variant="subtitle1">{homeTeamName}</Typography>
                                </Stack>
                                <Typography variant="h6" fontWeight="bold">
                                  {homeScore != null ? homeScore : '-'} - {awayScore != null ? awayScore : '-'}
                                </Typography>
                                <Stack direction="row" spacing={1} alignItems="center">
                                  <Typography variant="subtitle1">{awayTeamName}</Typography>
                                  <Avatar src={awayTeamLogo || undefined} alt={awayTeamName} />
                                </Stack>
                              </Stack>

                              <Stack direction="row" spacing={2} alignItems="center" color="text.secondary">
                                {kickoffDate && (
                                  <Stack direction="row" spacing={0.5} alignItems="center">
                                    <CalendarMonthIcon fontSize="small" />
                                    <Typography variant="body2">
                                      {kickoffDate.toLocaleString('tr-TR')}
                                    </Typography>
                                  </Stack>
                                )}
                              </Stack>

                              <Divider />
                              <Box display="flex" justifyContent="space-between" alignItems="center">
                                <Typography variant="body2" color="text.secondary">
                                  Yorumlar ve detaylar için aç
                                </Typography>
                                <Button
                                  variant="contained"
                                  color="primary"
                                  href={`/app/matches/${match.id}`}
                                >
                                  Detaylara Git
                                </Button>
                              </Box>
                            </Stack>
                          </CardContent>
                        </Card>
                      </Grid>
                    );
                  })}
                </Grid>
              </Box>
            )}
          </Box>
        )}

        {!loading && !error && filteredMatches.length === 0 && (
          <Box textAlign="center" py={4}>
            <Typography variant="h6" color="text.secondary">
              Aramanızla eşleşen maç bulunamadı.
            </Typography>
          </Box>
        )}
      </PapperBlock>
    </Container>
  );
}

export default MatchesPage;
