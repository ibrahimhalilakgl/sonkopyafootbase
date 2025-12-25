import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  CircularProgress,
  Typography,
  Alert,
  Stack,
  Avatar,
  Chip,
  Grid,
  Divider,
  Button,
  Link as MuiLink,
} from '@mui/material';
import PeopleIcon from '@mui/icons-material/People';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import BarChartIcon from '@mui/icons-material/BarChart';
import { teamsAPI } from 'utils/api';

function TeamDetailPage() {
  const { id } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [players, setPlayers] = useState([]);
  const [matches, setMatches] = useState([]);
  const [statistics, setStatistics] = useState(null);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const [teamRes, playersRes, matchesRes, statisticsRes] = await Promise.allSettled([
          teamsAPI.get(id),
          teamsAPI.getPlayers(id),
          teamsAPI.getMatches(id),
          teamsAPI.getStatistics(id),
        ]);
        
        if (teamRes.status === 'fulfilled' && mounted) {
          setData(teamRes.value);
        }
        if (playersRes.status === 'fulfilled' && mounted) {
          setPlayers(Array.isArray(playersRes.value) ? playersRes.value : []);
        }
        if (matchesRes.status === 'fulfilled' && mounted) {
          setMatches(Array.isArray(matchesRes.value) ? matchesRes.value : []);
        }
        if (statisticsRes.status === 'fulfilled' && mounted) {
          setStatistics(statisticsRes.value);
        }
      } catch (e) {
        if (mounted) setError('Veri yüklenemedi. Lütfen tekrar deneyin.');
      } finally {
        if (mounted) setLoading(false);
      }
    })();
    return () => {
      mounted = false;
    };
  }, [id]);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !data) {
    return (
      <Box mt={2}>
        <Alert severity="error">{error || 'Kayıt bulunamadı'}</Alert>
      </Box>
    );
  }

  return (
    <Box mt={2}>
      <Typography variant="h5" gutterBottom>
        Takım Detayı
      </Typography>
      <Card variant="outlined">
        <CardContent>
          <Stack spacing={2}>
            {(() => {
              const teamName = data.name || data.ad || 'Takım';
              const teamLogo = data.logoUrl || data.logo_url || data.logo || null;
              const displayName = teamName.substring(0, 2).toUpperCase();
              const leagueName = data.league || (data.lig && typeof data.lig === 'object' && data.lig.ligAdi ? data.lig.ligAdi : null) || '-';
              const stadiumObj = data.stadyum && typeof data.stadyum === 'object' ? data.stadyum : null;
              const stadiumName = data.stadium || (stadiumObj && stadiumObj.stadyumAdi ? stadiumObj.stadyumAdi : null) || '-';
              const city = data.city || data.sehir || (stadiumObj && stadiumObj.sehir ? stadiumObj.sehir : null) || '-';
              const teknikDirektorObj = data.teknikDirektor && typeof data.teknikDirektor === 'object' ? data.teknikDirektor : null;
              const teknikDirektorAdi = data.teknikDirektorAdi || (teknikDirektorObj && teknikDirektorObj.adSoyad ? teknikDirektorObj.adSoyad : null) || '-';
              const teknikDirektorUyruk = teknikDirektorObj && teknikDirektorObj.uyruk ? teknikDirektorObj.uyruk : null;
              return (
                <>
                  <Stack direction="row" spacing={2} alignItems="center">
                    <Avatar
                      src={teamLogo || undefined}
                      alt={teamName}
                      sx={{ width: 72, height: 72, bgcolor: 'primary.main' }}
                    >
                      {displayName}
                    </Avatar>
                    <Box>
                      <Typography variant="h6">{teamName}</Typography>
                      {data.aciklama && (
                        <Typography variant="body2" color="text.secondary" mt={0.5}>
                          {data.aciklama}
                        </Typography>
                      )}
                    </Box>
                  </Stack>
                  <Typography color="text.secondary">Lig: {leagueName}</Typography>
                  {city !== '-' && <Typography color="text.secondary">Şehir: {city}</Typography>}
                  {stadiumName !== '-' && <Typography color="text.secondary">Stadyum: {stadiumName}</Typography>}
                  {data.kurulusYili && <Typography color="text.secondary">Kuruluş: {data.kurulusYili}</Typography>}
                  {teknikDirektorAdi !== '-' && (
                    <Typography color="text.secondary">
                      Teknik Direktör: {teknikDirektorAdi}
                      {teknikDirektorUyruk && ` (${teknikDirektorUyruk})`}
                    </Typography>
                  )}
                  
                  {/* Takım İstatistikleri */}
                  {statistics && (
                    <>
                      <Divider />
                      <Typography variant="subtitle2" fontWeight="bold" color="text.secondary">
                        <BarChartIcon sx={{ verticalAlign: 'middle', mr: 0.5, fontSize: 18 }} />
                        İstatistikler
                      </Typography>
                      {statistics.toplam_mac !== undefined && (
                        <Typography color="text.secondary">Toplam Maç: {statistics.toplam_mac || 0}</Typography>
                      )}
                    </>
                  )}
                </>
              );
            })()}
          </Stack>
        </CardContent>
      </Card>

      {/* Takım Oyuncuları */}
      <Card variant="outlined" sx={{ mt: 2 }}>
        <CardContent>
          <Stack spacing={2}>
            <Typography variant="subtitle1" fontWeight="bold">
              <PeopleIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
              Takım Oyuncuları ({players.length})
            </Typography>
            {players.length > 0 ? (
              <Grid container spacing={2}>
                {players.map((player) => {
                  const fullName = player.fullName || (player.ad && player.soyad ? `${player.ad} ${player.soyad}` : 'İsimsiz');
                  const position = player.position || player.pozisyon || null;
                  const imageUrl = player.imageUrl || player.fotograf || null;
                  return (
                    <Grid item xs={12} sm={6} md={4} key={player.id}>
                      <Card variant="outlined">
                        <CardContent>
                          <Stack direction="row" spacing={2} alignItems="center">
                            <Avatar
                              src={imageUrl || undefined}
                              alt={fullName}
                              sx={{ width: 48, height: 48 }}
                            >
                              {fullName ? fullName.charAt(0).toUpperCase() : '?'}
                            </Avatar>
                            <Box flex={1}>
                              <MuiLink
                                component={Link}
                                to={`/app/players/${player.id}`}
                                underline="hover"
                                color="inherit"
                              >
                                <Typography variant="body1" fontWeight="medium">
                                  {fullName}
                                </Typography>
                              </MuiLink>
                              {position && (
                                <Chip label={position} size="small" variant="outlined" sx={{ mt: 0.5 }} />
                              )}
                            </Box>
                          </Stack>
                        </CardContent>
                      </Card>
                    </Grid>
                  );
                })}
              </Grid>
            ) : (
              <Typography color="text.secondary" variant="body2">
                Bu takımda henüz oyuncu kaydı yok.
              </Typography>
            )}
          </Stack>
        </CardContent>
      </Card>

      {/* Takım Oyuncuları - Eski Kod (Kaldırıldı) */}
      {false && players.length > 0 && (
        <Card variant="outlined" sx={{ mt: 2 }}>
          <CardContent>
            <Stack spacing={2}>
              <Typography variant="subtitle1" fontWeight="bold">
                <PeopleIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
                Takım Oyuncuları ({players.length})
              </Typography>
              <Grid container spacing={2}>
                {players.map((player) => {
                  const fullName = player.fullName || (player.ad && player.soyad ? `${player.ad} ${player.soyad}` : 'İsimsiz');
                  const position = player.position || player.pozisyon || null;
                  const imageUrl = player.imageUrl || player.fotograf || null;
                  return (
                    <Grid item xs={12} sm={6} md={4} key={player.id}>
                      <Card variant="outlined">
                        <CardContent>
                          <Stack direction="row" spacing={2} alignItems="center">
                            <Avatar
                              src={imageUrl || undefined}
                              alt={fullName}
                              sx={{ width: 48, height: 48 }}
                            >
                              {fullName ? fullName.charAt(0).toUpperCase() : '?'}
                            </Avatar>
                            <Box flex={1}>
                              <MuiLink
                                component={Link}
                                to={`/app/players/${player.id}`}
                                underline="hover"
                                color="inherit"
                              >
                                <Typography variant="body1" fontWeight="medium">
                                  {fullName}
                                </Typography>
                              </MuiLink>
                              {position && (
                                <Chip label={position} size="small" variant="outlined" sx={{ mt: 0.5 }} />
                              )}
                            </Box>
                          </Stack>
                        </CardContent>
                      </Card>
                    </Grid>
                  );
                })}
              </Grid>
            </Stack>
          </CardContent>
        </Card>
      )}

      {/* Takım Maçları */}
      <Card variant="outlined" sx={{ mt: 2 }}>
        <CardContent>
          <Stack spacing={2}>
            <Typography variant="subtitle1" fontWeight="bold">
              <SportsSoccerIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
              Takım Maçları ({matches.length})
            </Typography>
            {matches.length > 0 ? (
              <Stack spacing={1}>
                {matches.slice(0, 10).map((match) => {
                  const homeTeamName = match.homeTeam || (match.evSahibiTakim?.ad) || 'Bilinmiyor';
                  const awayTeamName = match.awayTeam || (match.deplasmanTakim?.ad) || 'Bilinmiyor';
                  const homeScore = match.homeScore != null ? match.homeScore : (match.evSahibiSkor != null ? match.evSahibiSkor : null);
                  const awayScore = match.awayScore != null ? match.awayScore : (match.deplasmanSkor != null ? match.deplasmanSkor : null);
                  let matchDate = null;
                  if (match.tarih && match.saat) {
                    matchDate = new Date(`${match.tarih}T${match.saat}`);
                  } else if (match.tarih) {
                    matchDate = new Date(match.tarih);
                  }
                  return (
                    <Box key={match.id} pb={1}>
                      <MuiLink
                        component={Link}
                        to={`/app/matches/${match.id}`}
                        underline="hover"
                        color="inherit"
                      >
                        <Stack direction="row" spacing={2} alignItems="center" justifyContent="space-between">
                          <Typography variant="body2">
                            {homeTeamName} vs {awayTeamName}
                          </Typography>
                          <Stack direction="row" spacing={1} alignItems="center">
                            <Typography variant="body2" fontWeight="bold">
                              {homeScore != null ? homeScore : '-'} - {awayScore != null ? awayScore : '-'}
                            </Typography>
                            {matchDate && (
                              <Typography variant="caption" color="text.secondary">
                                {matchDate.toLocaleDateString('tr-TR')}
                              </Typography>
                            )}
                          </Stack>
                        </Stack>
                      </MuiLink>
                      <Divider sx={{ mt: 1 }} />
                    </Box>
                  );
                })}
                {matches.length > 10 && (
                  <Typography variant="caption" color="text.secondary">
                    ... ve {matches.length - 10} maç daha
                  </Typography>
                )}
              </Stack>
            ) : (
              <Typography color="text.secondary" variant="body2">
                Bu takımın henüz maç kaydı yok.
              </Typography>
            )}
          </Stack>
        </CardContent>
      </Card>

      {/* Takım Maçları - Eski Kod (Kaldırıldı) */}
      {false && matches.length > 0 && (
        <Card variant="outlined" sx={{ mt: 2 }}>
          <CardContent>
            <Stack spacing={2}>
              <Typography variant="subtitle1" fontWeight="bold">
                <SportsSoccerIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
                Takım Maçları ({matches.length})
              </Typography>
              <Stack spacing={1}>
                {matches.slice(0, 10).map((match) => {
                  const homeTeamName = match.homeTeam || (match.evSahibiTakim?.ad) || 'Bilinmiyor';
                  const awayTeamName = match.awayTeam || (match.deplasmanTakim?.ad) || 'Bilinmiyor';
                  const homeScore = match.homeScore != null ? match.homeScore : (match.evSahibiSkor != null ? match.evSahibiSkor : null);
                  const awayScore = match.awayScore != null ? match.awayScore : (match.deplasmanSkor != null ? match.deplasmanSkor : null);
                  let matchDate = null;
                  if (match.tarih && match.saat) {
                    matchDate = new Date(`${match.tarih}T${match.saat}`);
                  } else if (match.tarih) {
                    matchDate = new Date(match.tarih);
                  }
                  return (
                    <Box key={match.id} pb={1}>
                      <MuiLink
                        component={Link}
                        to={`/app/matches/${match.id}`}
                        underline="hover"
                        color="inherit"
                      >
                        <Stack direction="row" spacing={2} alignItems="center" justifyContent="space-between">
                          <Typography variant="body2">
                            {homeTeamName} vs {awayTeamName}
                          </Typography>
                          <Stack direction="row" spacing={1} alignItems="center">
                            <Typography variant="body2" fontWeight="bold">
                              {homeScore != null ? homeScore : '-'} - {awayScore != null ? awayScore : '-'}
                            </Typography>
                            {matchDate && (
                              <Typography variant="caption" color="text.secondary">
                                {matchDate.toLocaleDateString('tr-TR')}
                              </Typography>
                            )}
                          </Stack>
                        </Stack>
                      </MuiLink>
                      <Divider sx={{ mt: 1 }} />
                    </Box>
                  );
                })}
              </Stack>
              {matches.length > 10 && (
                <Typography variant="caption" color="text.secondary">
                  ... ve {matches.length - 10} maç daha
                </Typography>
              )}
            </Stack>
          </CardContent>
        </Card>
      )}

      <Box mt={2}>
        <Link to="/app/teams">← Takım listesine dön</Link>
      </Box>
    </Box>
  );
}

export default TeamDetailPage;
