import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  CircularProgress,
  Typography,
  Alert,
  Divider,
  Stack,
  TextField,
  Button,
  IconButton,
  Tooltip,
  Avatar,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import YellowCardIcon from '@mui/icons-material/Error';
import RedCardIcon from '@mui/icons-material/Cancel';
import PhotoLibraryIcon from '@mui/icons-material/PhotoLibrary';
import HistoryIcon from '@mui/icons-material/History';
import { matchesAPI } from 'utils/api';

function MatchDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [commentText, setCommentText] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [matchTeams, setMatchTeams] = useState([]);
  const [matchEvents, setMatchEvents] = useState([]);
  const [matchMedia, setMatchMedia] = useState([]);
  const [statusHistory, setStatusHistory] = useState([]);

  const loadComments = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/matches/${id}/comments`);
      if (response.ok) {
        const commentsData = await response.json();
        setComments(Array.isArray(commentsData) ? commentsData : []);
      } else {
        setComments([]);
      }
    } catch (e) {
      console.error('Yorumlar yüklenemedi:', e);
      setComments([]);
    }
  };

  const load = async () => {
    try {
      setLoading(true);
      const matchRes = await matchesAPI.get(id);
      setData(matchRes);
      await loadComments();
      
      // Yeni entity'leri yükle
      try {
        const [teamsRes, eventsRes, mediaRes, historyRes] = await Promise.allSettled([
          matchesAPI.getTeams(id),
          matchesAPI.getEvents(id),
          matchesAPI.getMedia(id),
          matchesAPI.getStatusHistory(id),
        ]);
        
        if (teamsRes.status === 'fulfilled') {
          setMatchTeams(Array.isArray(teamsRes.value) ? teamsRes.value : []);
        } else {
          console.warn('Maç takımları yüklenemedi:', teamsRes.reason);
        }
        if (eventsRes.status === 'fulfilled') {
          setMatchEvents(Array.isArray(eventsRes.value) ? eventsRes.value : []);
        } else {
          console.warn('Maç olayları yüklenemedi:', eventsRes.reason);
        }
        if (mediaRes.status === 'fulfilled') {
          setMatchMedia(Array.isArray(mediaRes.value) ? mediaRes.value : []);
        } else {
          console.warn('Maç medyası yüklenemedi:', mediaRes.reason);
        }
        if (historyRes.status === 'fulfilled') {
          setStatusHistory(Array.isArray(historyRes.value) ? historyRes.value : []);
        } else {
          console.warn('Durum geçmişi yüklenemedi:', historyRes.reason);
        }
      } catch (e) {
        console.warn('Ek veriler yüklenemedi:', e);
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
    if (!commentText.trim()) return;
    try {
      setSubmitting(true);
      if (editingId) {
        await matchesAPI.updateComment(editingId, commentText.trim());
      } else {
        await matchesAPI.addComment(id, commentText.trim());
      }
      setCommentText('');
      setEditingId(null);
      await loadComments();
    } catch (e) {
      setError('Yorum kaydedilemedi. Lütfen tekrar deneyin.');
      if (e?.response?.status === 401) {
        navigate('/login');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = (comment) => {
    setEditingId(comment.id || comment.yorum_id);
    setCommentText(comment.message || comment.mesaj || comment.icerik || '');
  };

  const handleDelete = async (commentId) => {
    try {
      await matchesAPI.deleteComment(commentId);
      await loadComments();
    } catch (e) {
      setError('Yorum silinemedi.');
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
        Maç Detayı
      </Typography>
      <Card variant="outlined">
        <CardContent>
          <Stack spacing={2}>
            {(() => {
              // MacTakimlari tablosundan takım bilgilerini al
              let homeTeam = null;
              let awayTeam = null;
              if (matchTeams && matchTeams.length > 0) {
                homeTeam = matchTeams.find(t => t.evSahibi === true || t.ev_sahibi === true);
                awayTeam = matchTeams.find(t => t.evSahibi === false || t.ev_sahibi === false);
              }
              
              const homeTeamName = homeTeam?.takim?.ad || data.homeTeam || (data.evSahibiTakim && data.evSahibiTakim.ad ? data.evSahibiTakim.ad : 'Bilinmiyor');
              const awayTeamName = awayTeam?.takim?.ad || data.awayTeam || (data.deplasmanTakim && data.deplasmanTakim.ad ? data.deplasmanTakim.ad : 'Bilinmiyor');
              const homeTeamLogo = homeTeam?.takim?.logo || data.homeTeamLogo || (data.evSahibiTakim && data.evSahibiTakim.logo ? data.evSahibiTakim.logo : null);
              const awayTeamLogo = awayTeam?.takim?.logo || data.awayTeamLogo || (data.deplasmanTakim && data.deplasmanTakim.logo ? data.deplasmanTakim.logo : null);
              const homeScore = homeTeam?.skor != null ? homeTeam.skor : (data.homeScore != null ? data.homeScore : (data.evSahibiSkor != null ? data.evSahibiSkor : null));
              const awayScore = awayTeam?.skor != null ? awayTeam.skor : (data.awayScore != null ? data.awayScore : (data.deplasmanSkor != null ? data.deplasmanSkor : null));
              const status = data.status || data.durum || 'PLANLI';
              let kickoffDate = null;
              if (data.kickoffAt) {
                kickoffDate = new Date(data.kickoffAt);
              } else if (data.tarih && data.saat) {
                kickoffDate = new Date(`${data.tarih}T${data.saat}`);
              } else if (data.tarih) {
                kickoffDate = new Date(data.tarih);
              }
              const venue = data.venue || (data.evSahibiTakim && data.evSahibiTakim.stadyum && data.evSahibiTakim.stadyum.stadyumAdi ? data.evSahibiTakim.stadyum.stadyumAdi : null);
              return (
                <>
                  <Stack direction="row" spacing={2} alignItems="center" justifyContent="space-between">
                    <Stack direction="row" spacing={1} alignItems="center">
                      <Avatar src={homeTeamLogo || undefined} alt={homeTeamName} />
                      <Typography variant="h6" fontWeight="bold">{homeTeamName}</Typography>
                    </Stack>
                    <Stack direction="row" spacing={1} alignItems="center">
                      <SportsSoccerIcon color="primary" />
                      <Typography variant="h5" fontWeight="bold">
                        {homeScore != null ? homeScore : '-'} - {awayScore != null ? awayScore : '-'}
                      </Typography>
                    </Stack>
                    <Stack direction="row" spacing={1} alignItems="center">
                      <Typography variant="h6" fontWeight="bold">{awayTeamName}</Typography>
                      <Avatar src={awayTeamLogo || undefined} alt={awayTeamName} />
                    </Stack>
                  </Stack>
                  <Typography color="text.secondary">Durum: {status}</Typography>
                  <Typography color="text.secondary">
                    Başlama: {kickoffDate ? kickoffDate.toLocaleString('tr-TR') : '-'}
                  </Typography>
                  {venue && <Typography color="text.secondary">Stadyum: {venue}</Typography>}
                </>
              );
            })()}
            <Divider />
            
            {/* Maç Olayları (Gol, Kart) */}
            <>
              <Typography variant="subtitle1" fontWeight="bold">Maç Olayları</Typography>
              {matchEvents.length > 0 ? (
                <Stack spacing={1}>
                  {matchEvents.map((event) => {
                    const playerName = event.oyuncu ? `${event.oyuncu.ad} ${event.oyuncu.soyad}` : 'Bilinmiyor';
                    const eventType = event.olayTuru || event.olay_turu;
                    let icon = null;
                    if (eventType === 'GOL') {
                      icon = <EmojiEventsIcon color="warning" />;
                    } else if (eventType === 'SARI_KART') {
                      icon = <YellowCardIcon sx={{ color: '#ffc107' }} />;
                    } else if (eventType === 'KIRMIZI_KART') {
                      icon = <RedCardIcon color="error" />;
                    }
                    return (
                      <Box key={event.id} display="flex" alignItems="center" gap={1}>
                        {icon}
                        <Typography variant="body2">
                          <strong>{playerName}</strong> - {eventType === 'GOL' ? 'Gol' : eventType === 'SARI_KART' ? 'Sarı Kart' : 'Kırmızı Kart'}
                        </Typography>
                      </Box>
                    );
                  })}
                </Stack>
              ) : (
                <Typography color="text.secondary" variant="body2">
                  Henüz olay kaydedilmemiş.
                </Typography>
              )}
              <Divider />
            </>
            
            {/* Maç Medyası */}
            <>
              <Typography variant="subtitle1" fontWeight="bold">
                <PhotoLibraryIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
                Maç Medyası
              </Typography>
              {matchMedia.length > 0 ? (
                <Box display="flex" flexWrap="wrap" gap={2}>
                  {matchMedia.map((media) => (
                    <Box key={media.id} sx={{ maxWidth: 200 }}>
                      {media.tip === 'FOTO' || media.tip === 'IMAGE' ? (
                        <img
                          src={media.url}
                          alt="Maç medyası"
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
              <Divider />
            </>
            
            {/* Durum Geçmişi */}
            <>
              <Typography variant="subtitle1" fontWeight="bold">
                <HistoryIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
                Durum Geçmişi
              </Typography>
              {statusHistory.length > 0 ? (
                <Stack spacing={1}>
                  {statusHistory.map((history) => {
                    const historyDate = history.islemTarihi ? new Date(history.islemTarihi) : null;
                    const statusText = history.durum === 'ONAY_BEKLIYOR' ? 'Onay Bekliyor' : 
                                     history.durum === 'YAYINDA' ? 'Yayında' : 
                                     history.durum === 'REDDEDILDI' ? 'Reddedildi' : history.durum;
                    return (
                      <Box key={history.id}>
                        <Typography variant="body2">
                          <strong>{statusText}</strong>
                          {historyDate && ` - ${historyDate.toLocaleString('tr-TR')}`}
                        </Typography>
                      </Box>
                    );
                  })}
                </Stack>
              ) : (
                <Typography color="text.secondary" variant="body2">
                  Durum geçmişi kaydı yok.
                </Typography>
              )}
              <Divider />
            </>
            
            <Typography variant="subtitle1" fontWeight="bold">Yorumlar</Typography>
            {comments.map((c) => {
              const message = c.message || c.mesaj || c.icerik || '';
              const author = c.author || (c.kullanici && c.kullanici.kullaniciAdi ? c.kullanici.kullaniciAdi : 'Anonim');
              let commentDate = null;
              if (c.createdAt) {
                commentDate = new Date(c.createdAt);
              } else if (c.yorumTarihi) {
                commentDate = new Date(c.yorumTarihi);
              } else if (c.olusturmaTarihi) {
                commentDate = new Date(c.olusturmaTarihi);
              }
              return (
                <Box key={c.id || c.yorum_id} pb={1}>
                  <Stack direction="row" alignItems="center" justifyContent="space-between">
                    <Box>
                      <Typography variant="body1" fontWeight="bold">{author}</Typography>
                      <Typography variant="body2" color="text.secondary">
                        {message}
                      </Typography>
                      {commentDate && (
                        <Typography variant="caption" color="text.secondary">
                          {commentDate.toLocaleString('tr-TR')}
                        </Typography>
                      )}
                    </Box>
                    {c.canEdit && (
                      <Stack direction="row" spacing={1}>
                        <Tooltip title="Düzenle">
                          <IconButton size="small" onClick={() => handleEdit(c)}>
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Sil">
                          <IconButton size="small" onClick={() => handleDelete(c.id || c.yorum_id)}>
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </Stack>
                    )}
                  </Stack>
                </Box>
              );
            })}
            {comments.length === 0 && <Typography color="text.secondary">Henüz yorum yok.</Typography>}
            <Divider />
            <Typography variant="subtitle1" fontWeight="bold">Yorum ekle</Typography>
            <TextField
              multiline
              minRows={2}
              fullWidth
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              placeholder="Yorumunuzu yazın"
            />
            <Box display="flex" justifyContent="flex-end" gap={1}>
              {editingId && (
                <Button
                  variant="text"
                  onClick={() => { setEditingId(null); setCommentText(''); }}
                  disabled={submitting}
                >
                  İptal
                </Button>
              )}
              <Button
                variant="contained"
                onClick={handleSubmit}
                disabled={submitting || !commentText.trim()}
              >
                {editingId ? 'Güncelle' : 'Gönder'}
              </Button>
            </Box>
          </Stack>
        </CardContent>
      </Card>
      <Box mt={2}>
        <Link to="/app/matches">← Maç listesine dön</Link>
      </Box>
    </Box>
  );
}

export default MatchDetailPage;
