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
  Divider,
  TextField,
  Button,
  Rating,
  CardMedia,
  Chip,
  Grid,
  Avatar,
} from '@mui/material';
import PhotoLibraryIcon from '@mui/icons-material/PhotoLibrary';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import YellowCardIcon from '@mui/icons-material/Error';
import RedCardIcon from '@mui/icons-material/Cancel';
import BarChartIcon from '@mui/icons-material/BarChart';
import CommentIcon from '@mui/icons-material/Comment';
import { playersAPI } from 'utils/api';

function PlayerDetailPage() {
  const { id } = useParams();
  const [data, setData] = useState(null);
  const [ratings, setRatings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [score, setScore] = useState(7);
  const [comment, setComment] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const [playerScore, setPlayerScore] = useState(null);
  const [playerMedia, setPlayerMedia] = useState([]);
  const [playerStatistics, setPlayerStatistics] = useState(null);
  const [playerComments, setPlayerComments] = useState([]);

  const load = async () => {
    try {
      setLoading(true);
      const [playerRes, ratingRes, scoreRes, mediaRes, statisticsRes, commentsRes] = await Promise.allSettled([
        playersAPI.get(id),
        playersAPI.getRatings(id),
        playersAPI.getScore(id),
        playersAPI.getMedia(id),
        playersAPI.getStatistics(id),
        playersAPI.getComments(id),
      ]);
      
      if (playerRes.status === 'fulfilled') {
        setData(playerRes.value);
      }
      if (ratingRes.status === 'fulfilled') {
        setRatings(ratingRes.value || []);
      }
      if (scoreRes.status === 'fulfilled') {
        setPlayerScore(scoreRes.value);
      }
      if (mediaRes.status === 'fulfilled') {
        setPlayerMedia(Array.isArray(mediaRes.value) ? mediaRes.value : []);
      } else {
        console.warn('Oyuncu medyası yüklenemedi:', mediaRes.reason);
      }
      if (statisticsRes.status === 'fulfilled') {
        setPlayerStatistics(statisticsRes.value);
      } else {
        console.warn('Oyuncu istatistikleri yüklenemedi:', statisticsRes.reason);
      }
      if (commentsRes.status === 'fulfilled') {
        setPlayerComments(Array.isArray(commentsRes.value) ? commentsRes.value : []);
      } else {
        console.warn('Oyuncu yorumları yüklenemedi:', commentsRes.reason);
      }
      
      setError(null);
    } catch (e) {
      setError('Veri yüklenemedi. Lütfen tekrar deneyin.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let mounted = true;
    (async () => {
      if (!mounted) return;
      await load();
    })();
    return () => {
      mounted = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleSubmit = async () => {
    try {
      setSubmitting(true);
      await playersAPI.rate(id, score, comment.trim());
      setComment('');
      await load();
    } catch (e) {
      setError('Değerlendirme eklenemedi. Lütfen tekrar deneyin.');
    } finally {
      setSubmitting(false);
    }
  };

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
        Oyuncu Detayı
      </Typography>

      <Card sx={{ mb: 2 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={4} md={3}>
            {(() => {
              const imageUrl = data.imageUrl || data.fotograf || null;
              const fullName = data.fullName || (data.ad && data.soyad ? `${data.ad} ${data.soyad}` : 'İsimsiz');
              return imageUrl ? (
                <CardMedia
                  component="img"
                  image={imageUrl}
                  alt={fullName}
                  sx={{
                    height: { xs: 260, sm: 320 },
                    width: '100%',
                    objectFit: 'contain',
                    objectPosition: 'top center',
                    borderRadius: 1,
                  }}
                />
              ) : (
                <Box py={3} display="flex" justifyContent="center">
                  <Avatar sx={{ width: 120, height: 120, fontSize: 48 }}>
                    {fullName ? fullName.charAt(0).toUpperCase() : '?'}
                  </Avatar>
                </Box>
              );
            })()}
          </Grid>
          <Grid item xs={12} sm={8} md={9}>
            <CardContent>
              <Stack spacing={1.5}>
                {(() => {
                  const fullName = data.fullName || (data.ad && data.soyad ? `${data.ad} ${data.soyad}` : 'İsimsiz');
                  const teamName = data.team || (data.takim && data.takim.ad ? data.takim.ad : null);
                  const position = data.position || data.pozisyon || null;
                  return (
                    <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
                      <Typography variant="h5" fontWeight="bold">
                        {fullName}
                      </Typography>
                      {teamName && <Chip label={teamName} color="primary" size="small" />}
                      {position && <Chip label={position} variant="outlined" size="small" />}
                    </Stack>
                  );
                })()}
                <Stack direction={{ xs: 'column', sm: 'row' }} spacing={3}>
                  <Stack spacing={0.5}>
                    <Typography color="text.secondary">Ortalama Puan</Typography>
                    <Stack direction="row" spacing={1} alignItems="center">
                      <Rating value={playerScore?.puan ? parseFloat(playerScore.puan) / 10 : (data.averageRating || 0)} max={10} precision={0.1} readOnly />
                      <Typography variant="subtitle1" fontWeight="bold">
                        {playerScore?.puan ? parseFloat(playerScore.puan).toFixed(2) : ((data.averageRating || 0).toFixed(1))}
                      </Typography>
                    </Stack>
                    <Typography variant="body2" color="text.secondary">
                      {data.ratingCount || 0} değerlendirme
                    </Typography>
                  </Stack>
                  
                  {/* Oyuncu İstatistikleri */}
                  <Stack spacing={0.5}>
                    <Typography color="text.secondary">
                      <BarChartIcon sx={{ verticalAlign: 'middle', mr: 0.5 }} />
                      İstatistikler
                    </Typography>
                    <Stack direction="row" spacing={2}>
                      <Box>
                        <Typography variant="h6" color="primary">
                          {playerStatistics?.toplam_gol ?? 0}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          <EmojiEventsIcon sx={{ fontSize: 14, verticalAlign: 'middle', mr: 0.5 }} />
                          Gol
                        </Typography>
                      </Box>
                      <Box>
                        <Typography variant="h6" color="warning.main">
                          {playerStatistics?.toplam_sari_kart ?? 0}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          <YellowCardIcon sx={{ fontSize: 14, verticalAlign: 'middle', mr: 0.5, color: '#ffc107' }} />
                          Sarı Kart
                        </Typography>
                      </Box>
                      <Box>
                        <Typography variant="h6" color="error.main">
                          {playerStatistics?.toplam_kirmizi_kart ?? 0}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          <RedCardIcon sx={{ fontSize: 14, verticalAlign: 'middle', mr: 0.5 }} />
                          Kırmızı Kart
                        </Typography>
                      </Box>
                    </Stack>
                  </Stack>
                </Stack>
              </Stack>
            </CardContent>
          </Grid>
        </Grid>
      </Card>

      {/* Oyuncu Medyası */}
      <Card variant="outlined" sx={{ mb: 2 }}>
        <CardContent>
          <Stack spacing={2}>
            <Typography variant="subtitle1" fontWeight="bold">
              <PhotoLibraryIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
              Oyuncu Medyası
            </Typography>
            {playerMedia.length > 0 ? (
              <Box display="flex" flexWrap="wrap" gap={2}>
                {playerMedia.map((media) => (
                  <Box key={media.medya_id || media.id} sx={{ maxWidth: 200 }}>
                    {media.tip === 'IMAGE' || media.tip === 'FOTO' ? (
                      <img
                        src={media.url}
                        alt="Oyuncu medyası"
                        style={{ width: '100%', height: 'auto', borderRadius: 4, cursor: 'pointer' }}
                        onError={(e) => { e.target.style.display = 'none'; }}
                        onClick={() => window.open(media.url, '_blank')}
                      />
                    ) : (
                      <Box sx={{ p: 2, border: 1, borderRadius: 1, cursor: 'pointer' }} onClick={() => window.open(media.url, '_blank')}>
                        <Typography variant="caption">Video: {media.url}</Typography>
                      </Box>
                    )}
                  </Box>
                ))}
              </Box>
            ) : (
              <Typography color="text.secondary" variant="body2">
                Henüz medya eklenmemiş.
              </Typography>
            )}
          </Stack>
        </CardContent>
      </Card>

      {/* Oyuncu Medyası - Eski Kod (Kaldırıldı) */}
      {false && playerMedia.length > 0 && (
        <Card variant="outlined" sx={{ mb: 2 }}>
          <CardContent>
            <Stack spacing={2}>
              <Typography variant="subtitle1" fontWeight="bold">
                <PhotoLibraryIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
                Oyuncu Medyası
              </Typography>
              <Box display="flex" flexWrap="wrap" gap={2}>
                {playerMedia.map((media) => (
                  <Box key={media.medya_id || media.id} sx={{ maxWidth: 200 }}>
                    {media.tip === 'IMAGE' || media.tip === 'FOTO' ? (
                      <img
                        src={media.url}
                        alt="Oyuncu medyası"
                        style={{ width: '100%', height: 'auto', borderRadius: 4, cursor: 'pointer' }}
                        onError={(e) => { e.target.style.display = 'none'; }}
                        onClick={() => window.open(media.url, '_blank')}
                      />
                    ) : (
                      <Box sx={{ p: 2, border: 1, borderRadius: 1, cursor: 'pointer' }} onClick={() => window.open(media.url, '_blank')}>
                        <Typography variant="caption">Video: {media.url}</Typography>
                      </Box>
                    )}
                  </Box>
                ))}
              </Box>
            </Stack>
          </CardContent>
        </Card>
      )}

      {/* Oyuncu Yorumları */}
      <Card variant="outlined" sx={{ mb: 2 }}>
        <CardContent>
          <Stack spacing={1.5}>
            <Typography variant="subtitle1" fontWeight="bold">
              <CommentIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
              Oyuncu Yorumları
            </Typography>
            {playerComments.length > 0 ? (
              playerComments.map((comment) => {
                const commentText = comment.icerik || comment.comment || '';
                const author = comment.kullanici?.kullaniciAdi || comment.author || 'Anonim';
                const commentDate = comment.olusturmaTarihi ? new Date(comment.olusturmaTarihi) : null;
                return (
                  <Box key={comment.yorum_id || comment.id} pb={1}>
                    <Stack direction="row" alignItems="center" spacing={1} mb={0.5}>
                      <Typography variant="body2" fontWeight="bold">{author}</Typography>
                      {commentDate && (
                        <Typography variant="caption" color="text.secondary">
                          {commentDate.toLocaleString('tr-TR')}
                        </Typography>
                      )}
                    </Stack>
                    <Typography variant="body2" color="text.primary">
                      {commentText}
                    </Typography>
                    <Divider sx={{ mt: 1 }} />
                  </Box>
                );
              })
            ) : (
              <Typography color="text.secondary" variant="body2">
                Henüz yorum yapılmamış.
              </Typography>
            )}
          </Stack>
        </CardContent>
      </Card>

      {/* Oyuncu Yorumları - Eski Kod (Kaldırıldı) */}
      {false && playerComments.length > 0 && (
        <Card variant="outlined" sx={{ mb: 2 }}>
          <CardContent>
            <Stack spacing={1.5}>
              <Typography variant="subtitle1" fontWeight="bold">
                <CommentIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
                Oyuncu Yorumları
              </Typography>
              {playerComments.map((comment) => {
                const commentText = comment.icerik || comment.comment || '';
                const author = comment.kullanici?.kullaniciAdi || comment.author || 'Anonim';
                const commentDate = comment.olusturmaTarihi ? new Date(comment.olusturmaTarihi) : null;
                return (
                  <Box key={comment.yorum_id || comment.id} pb={1}>
                    <Stack direction="row" alignItems="center" spacing={1} mb={0.5}>
                      <Typography variant="body2" fontWeight="bold">{author}</Typography>
                      {commentDate && (
                        <Typography variant="caption" color="text.secondary">
                          {commentDate.toLocaleString('tr-TR')}
                        </Typography>
                      )}
                    </Stack>
                    <Typography variant="body2" color="text.primary">
                      {commentText}
                    </Typography>
                    <Divider sx={{ mt: 1 }} />
                  </Box>
                );
              })}
            </Stack>
          </CardContent>
        </Card>
      )}

      <Card variant="outlined" sx={{ mb: 2 }}>
        <CardContent>
          <Stack spacing={1.5}>
            <Typography variant="subtitle1">Değerlendirmeler</Typography>
            {(ratings || []).map((r) => {
              // Yorum içeriğinden puan bilgisini çıkar (format: [8/10] yorum metni)
              let score = r.score || null;
              let commentText = r.comment || r.icerik || '';
              if (!score && commentText.startsWith('[') && commentText.includes('/10]')) {
                try {
                  const match = commentText.match(/\[(\d+(?:\.\d+)?)\/10\]/);
                  if (match) {
                    score = parseFloat(match[1]);
                    commentText = commentText.replace(/\[\d+(?:\.\d+)?\/10\]\s*/, '').trim();
                  }
                } catch (e) {
                  // Hata olursa score null kalır
                }
              }
              return (
                <Box key={r.id || r.yorum_id} pb={1}>
                  <Stack direction="row" alignItems="center" spacing={1}>
                    {score && <Rating size="small" value={Number(score)} readOnly max={10} />}
                    <Typography variant="body2" color="text.secondary">
                      {r.author || (r.kullanici && r.kullanici.kullaniciAdi) || '-'}
                    </Typography>
                    {r.olusturmaTarihi && (
                      <Typography variant="caption" color="text.secondary">
                        {new Date(r.olusturmaTarihi).toLocaleString('tr-TR')}
                      </Typography>
                    )}
                  </Stack>
                  {commentText && (
                    <Typography variant="body2" color="text.primary">
                      {commentText}
                    </Typography>
                  )}
                </Box>
              );
            })}
            {(ratings || []).length === 0 && (
              <Typography color="text.secondary">Değerlendirme yok</Typography>
            )}
          </Stack>
        </CardContent>
      </Card>

      <Card variant="outlined">
        <CardContent>
          <Stack spacing={1.5}>
            <Typography variant="subtitle1">Değerlendirme ekle</Typography>
            <Stack direction="row" alignItems="center" spacing={1}>
              <Rating
                value={score}
                max={10}
                onChange={(_, val) => setScore(val || 1)}
              />
              <Typography>{score}</Typography>
            </Stack>
            <TextField
              multiline
              minRows={2}
              fullWidth
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Yorum (opsiyonel)"
            />
            <Box display="flex" justifyContent="flex-end">
              <Button
                variant="contained"
                onClick={handleSubmit}
                disabled={submitting}
              >
                Gönder
              </Button>
            </Box>
          </Stack>
        </CardContent>
      </Card>

      <Box mt={2}>
        <Link to="/app/players">← Oyuncu listesine dön</Link>
      </Box>
    </Box>
  );
}

export default PlayerDetailPage;
