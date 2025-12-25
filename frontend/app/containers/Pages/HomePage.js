import React, { useEffect, useState } from 'react';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Divider from '@mui/material/Divider';
import { useTheme } from '@mui/material/styles';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import Avatar from '@mui/material/Avatar';
import Chip from '@mui/material/Chip';
import Button from '@mui/material/Button';
import Rating from '@mui/material/Rating';
import { homeAPI } from 'utils/api';

function HomePage() {
  const theme = useTheme();
  const [upcomingMatches, setUpcomingMatches] = useState([]);
  const [comments, setComments] = useState([]);
  const [playerCount, setPlayerCount] = useState(0);
  const [teamCount, setTeamCount] = useState(0);
  const [topPlayer, setTopPlayer] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let mounted = true;
    async function load() {
      try {
        const data = await homeAPI.get();
        if (!mounted) return;
        setUpcomingMatches(data.upcomingMatches || data.matches || []);
        setComments(data.comments || []);
        setPlayerCount(data.playerCount || 0);
        setTeamCount(data.teamCount || 0);
        setTopPlayer(data.topRatedPlayer || null);
      } catch (err) {
        if (mounted) setError('Veri yüklenemedi. Lütfen tekrar deneyin.');
      } finally {
        if (mounted) setLoading(false);
      }
    }
    load();
    return () => {
      mounted = false;
    };
  }, []);

  const section = (title, content) => (
    <Card variant="outlined" sx={{ height: '100%' }}>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          {title}
        </Typography>
        {content}
      </CardContent>
    </Card>
  );

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box mt={2}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  return (
    <Box mt={2}>
      <Box
        sx={{
          mb: 3,
          p: { xs: 3, md: 4 },
          borderRadius: 2,
          background: `linear-gradient(120deg, ${theme.palette.primary.dark} 0%, ${theme.palette.primary.main} 40%, ${theme.palette.secondary.main} 100%)`,
          color: theme.palette.getContrastText(theme.palette.primary.main),
        }}
      >
        <Grid container spacing={3} alignItems="center">
          <Grid item xs={12} md={8}>
            <Stack spacing={2}>
              <Typography variant="h4" fontWeight="bold">
                FootBase: Türkiye futbolunun canlı veritabanı
              </Typography>
              <Typography variant="body1" sx={{ opacity: 0.9 }}>
                Maç programları, skorlar, oyuncu profilleri ve topluluk yorumlarını tek ekranda takip et.
                Verilerini kendi API’nden alan, taraftara odaklı deneyim.
              </Typography>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
                <Button variant="contained" color="secondary" href="/app/matches">
                  Maçları Gör
                </Button>
                <Button
                  variant="outlined"
                  color="inherit"
                  href="/app/players"
                  sx={{ borderColor: theme.palette.common.white, color: theme.palette.common.white }}
                >
                  Oyuncuları Keşfet
                </Button>
                <Button
                  variant="outlined"
                  color="inherit"
                  href="/app/teams"
                  sx={{ borderColor: theme.palette.common.white, color: theme.palette.common.white }}
                >
                  Takımlar
                </Button>
              </Stack>
            </Stack>
          </Grid>
          <Grid item xs={12} md={4}>
            <Stack spacing={1.5}>
              <Card sx={{ background: 'rgba(255,255,255,0.12)', color: theme.palette.common.white }} variant="outlined">
                <CardContent>
                  <Typography variant="overline" sx={{ opacity: 0.8 }}>
                    Oyuncu sayısı
                  </Typography>
                  <Typography variant="h4" fontWeight="bold">
                    {playerCount}
                  </Typography>
                  <Typography variant="body2" sx={{ opacity: 0.8 }}>
                    Veritabanındaki toplam oyuncu
                  </Typography>
                </CardContent>
              </Card>
              <Card sx={{ background: 'rgba(255,255,255,0.12)', color: theme.palette.common.white }} variant="outlined">
                <CardContent>
                  <Typography variant="overline" sx={{ opacity: 0.8 }}>
                    Takım sayısı
                  </Typography>
                  <Typography variant="h4" fontWeight="bold">
                    {teamCount}
                  </Typography>
                  <Typography variant="body2" sx={{ opacity: 0.8 }}>
                    Kayıtlı profesyonel kulüp
                  </Typography>
                </CardContent>
              </Card>
              {topPlayer && (() => {
                const fullName = topPlayer.fullName || (topPlayer.ad && topPlayer.soyad ? `${topPlayer.ad} ${topPlayer.soyad}` : 'İsimsiz');
                const teamName = topPlayer.team || (topPlayer.takim && topPlayer.takim.ad ? topPlayer.takim.ad : 'Takım bilgisi yok');
                const imageUrl = topPlayer.imageUrl || topPlayer.fotograf || null;
                return (
                  <Card sx={{ background: 'rgba(255,255,255,0.12)', color: theme.palette.common.white }} variant="outlined">
                    <CardContent>
                      <Typography variant="overline" sx={{ opacity: 0.8 }}>
                        En yüksek puanlı oyuncu
                      </Typography>
                      <Stack direction="row" spacing={1.5} alignItems="center" mt={1}>
                        <Avatar
                          src={imageUrl || undefined}
                          alt={fullName}
                          sx={{ width: 48, height: 48 }}
                        >
                          {fullName ? fullName.charAt(0).toUpperCase() : '?'}
                        </Avatar>
                        <Stack spacing={0.5}>
                          <Typography variant="subtitle1" fontWeight="bold">
                            {fullName}
                          </Typography>
                          <Typography variant="body2" sx={{ opacity: 0.8 }}>
                            {teamName}
                          </Typography>
                          <Stack direction="row" spacing={1} alignItems="center">
                            <Rating value={topPlayer.averageRating || 0} max={10} precision={0.1} readOnly size="small" />
                            <Typography variant="subtitle2" fontWeight="bold">
                              {(topPlayer.averageRating || 0).toFixed(1)}
                            </Typography>
                            <Typography variant="caption" sx={{ opacity: 0.8 }}>
                              ({topPlayer.ratingCount || 0})
                            </Typography>
                          </Stack>
                        </Stack>
                      </Stack>
                    </CardContent>
                  </Card>
                );
              })()}
            </Stack>
          </Grid>
        </Grid>
      </Box>
      <Typography variant="h5" gutterBottom>
        Özet Akışı
      </Typography>
      <Grid container spacing={2}>
        <Grid item xs={12} md={6}>
          {section(
            'Son Yorumlar',
            <List dense>
              {comments.map((c, idx) => {
                const message = c.message || c.mesaj || 'Yeni yorum';
                const author = c.author || 'Anonim';
                let commentDate = null;
                if (c.createdAt) {
                  commentDate = new Date(c.createdAt);
                } else if (c.yorumTarihi) {
                  commentDate = new Date(c.yorumTarihi);
                }
                return (
                  <React.Fragment key={`${c.id || idx}`}>
                    <ListItem alignItems="flex-start">
                      <ListItemText
                        primary={message}
                        secondary={[
                          author,
                          c.macBilgisi ? `Maç: ${c.macBilgisi}` : null,
                          commentDate ? commentDate.toLocaleString('tr-TR') : null,
                        ]
                          .filter(Boolean)
                          .join(' • ')}
                      />
                    </ListItem>
                    <Divider component="li" />
                  </React.Fragment>
                );
              })}
              {comments.length === 0 && (
                <ListItem>
                  <ListItemText primary="Henüz yorum yok." />
                </ListItem>
              )}
            </List>,
          )}
        </Grid>
        <Grid item xs={12} md={6}>
          {section(
            'Yaklaşan Maçlar',
            <Stack spacing={2}>
              {upcomingMatches.map((m) => {
                const homeTeamName = m.homeTeam || (m.evSahibiTakim && m.evSahibiTakim.ad ? m.evSahibiTakim.ad : 'Bilinmiyor');
                const awayTeamName = m.awayTeam || (m.deplasmanTakim && m.deplasmanTakim.ad ? m.deplasmanTakim.ad : 'Bilinmiyor');
                const homeTeamLogo = m.homeTeamLogo || (m.evSahibiTakim && m.evSahibiTakim.logo ? m.evSahibiTakim.logo : null);
                const awayTeamLogo = m.awayTeamLogo || (m.deplasmanTakim && m.deplasmanTakim.logo ? m.deplasmanTakim.logo : null);
                const homeScore = m.homeScore != null ? m.homeScore : (m.evSahibiSkor != null ? m.evSahibiSkor : null);
                const awayScore = m.awayScore != null ? m.awayScore : (m.deplasmanSkor != null ? m.deplasmanSkor : null);
                const status = m.status || m.durum || 'PLANLI';
                let kickoffDate = null;
                if (m.kickoffAt) {
                  kickoffDate = new Date(m.kickoffAt);
                } else if (m.tarih && m.saat) {
                  kickoffDate = new Date(`${m.tarih}T${m.saat}`);
                } else if (m.tarih) {
                  kickoffDate = new Date(m.tarih);
                }
                return (
                  <Card key={m.id} variant="outlined">
                    <CardContent>
                      <Stack spacing={1.5}>
                        <Stack direction="row" spacing={2} alignItems="center" justifyContent="space-between">
                          <Stack direction="row" spacing={1.5} alignItems="center">
                            <Avatar src={homeTeamLogo || undefined} alt={homeTeamName} />
                            <Typography variant="subtitle1" fontWeight="bold">{homeTeamName}</Typography>
                          </Stack>
                          <Typography variant="h6" fontWeight="bold">
                            {homeScore != null ? homeScore : '-'} - {awayScore != null ? awayScore : '-'}
                          </Typography>
                          <Stack direction="row" spacing={1.5} alignItems="center">
                            <Typography variant="subtitle1" fontWeight="bold">{awayTeamName}</Typography>
                            <Avatar src={awayTeamLogo || undefined} alt={awayTeamName} />
                          </Stack>
                        </Stack>
                        <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
                          <Typography color="text.secondary">
                            {kickoffDate ? kickoffDate.toLocaleString('tr-TR') : 'Tarih bekleniyor'}
                          </Typography>
                          <Chip label={status} size="small" />
                        </Stack>
                      </Stack>
                    </CardContent>
                  </Card>
                );
              })}
              {upcomingMatches.length === 0 && (
                <Typography color="text.secondary">Planlanan maç bulunamadı.</Typography>
              )}
            </Stack>,
          )}
        </Grid>
        <Grid item xs={12} md={6}>
          {section(
            'FootBase Hakkında',
            <Stack spacing={1.5}>
              <Typography variant="body1">
                FootBase, Türk futbolunu tek bir veri katmanında toplayan taraftar odaklı platformdur.
                Takvim, skor, oyuncu profili ve yorumlar tek API ve tek arayüzde birleşiyor.
              </Typography>
              <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                <Chip label="Gerçek zamanlı skorlar" color="primary" variant="outlined" />
                <Chip label="Oyuncu profilleri" color="primary" variant="outlined" />
                <Chip label="Topluluk yorumları" color="primary" variant="outlined" />
                <Chip label="Takım verileri" color="primary" variant="outlined" />
              </Stack>
              <Typography variant="body2" color="text.secondary">
                Yol haritası: daha fazla lig desteği, gelişmiş istatistikler ve kişiselleştirilmiş bildirimler.
              </Typography>
            </Stack>,
          )}
        </Grid>
        <Grid item xs={12} md={6}>
          {section(
            'Hızlı Erişim',
            <Stack spacing={2}>
              <Button variant="contained" color="primary" href="/app/matches" fullWidth>
                Maç Fikstürü ve Skorlar
              </Button>
              <Button variant="outlined" color="primary" href="/app/players" fullWidth>
                Oyuncu Listesi
              </Button>
              <Button variant="outlined" color="primary" href="/app/teams" fullWidth>
                Takım Sayfası
              </Button>
              <Typography variant="body2" color="text.secondary">
                Tüm veri FootBase’in kendi veritabanından beslenir, harici API bağımlılığı yoktur.
              </Typography>
            </Stack>,
          )}
        </Grid>
      </Grid>
    </Box>
  );
}

export default HomePage;
