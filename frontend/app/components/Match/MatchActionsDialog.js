import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Grid,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Typography,
  Box,
  Alert,
  Chip,
} from '@mui/material';
import SportsSoccerIcon from '@mui/icons-material/SportsSoccer';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import StopIcon from '@mui/icons-material/Stop';
import { editorAPI, teamsAPI } from 'utils/api';

/**
 * Maç Aksiyonları Dialog
 * Gol girişi, skor güncelleme, maç başlatma/bitirme
 */
function MatchActionsDialog({ open, onClose, match, onSuccess }) {
  const [tab, setTab] = useState('score'); // 'score' | 'goal' | 'control'
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  
  // Skor güncelleme
  const [evSahibiSkor, setEvSahibiSkor] = useState(0);
  const [deplasmanSkor, setDeplasmanSkor] = useState(0);
  
  // Gol ekleme
  const [oyuncuId, setOyuncuId] = useState('');
  const [dakika, setDakika] = useState('');
  const [players, setPlayers] = useState([]);
  
  // Maç zamanı kontrolü
  const [canManageMatch, setCanManageMatch] = useState(false);
  
  useEffect(() => {
    if (match) {
      setEvSahibiSkor(match.evSahibiSkor || 0);
      setDeplasmanSkor(match.deplasmanSkor || 0);
      
      // Maç başlangıç saati geçti mi kontrol et
      const now = new Date();
      const matchDateTime = new Date(`${match.tarih}T${match.saat}`);
      setCanManageMatch(now >= matchDateTime);
      
      // Oyuncuları yükle
      loadPlayers();
    }
  }, [match]);
  
  const loadPlayers = async () => {
    if (!match) return;
    try {
      const [homePlayers, awayPlayers] = await Promise.all([
        teamsAPI.getPlayers(match.evSahibiTakim?.id),
        teamsAPI.getPlayers(match.deplasmanTakim?.id)
      ]);
      setPlayers([...(homePlayers || []), ...(awayPlayers || [])]);
    } catch (err) {
      console.error('Oyuncular yüklenemedi:', err);
    }
  };
  
  const handleSkorGuncelle = async () => {
    try {
      setLoading(true);
      setError(null);
      await editorAPI.updateScore(match.id, {
        evSahibiSkor: parseInt(evSahibiSkor),
        deplasmanSkor: parseInt(deplasmanSkor)
      });
      setSuccess('Skor başarıyla güncellendi!');
      setTimeout(() => {
        onSuccess();
        onClose();
      }, 1500);
    } catch (err) {
      setError(err.hata || 'Skor güncellenemedi');
    } finally {
      setLoading(false);
    }
  };
  
  const handleGolEkle = async () => {
    if (!oyuncuId || !dakika) {
      setError('Lütfen oyuncu ve dakika seçin');
      return;
    }
    try {
      setLoading(true);
      setError(null);
      await editorAPI.addMatchEvent(match.id, {
        oyuncu: { id: parseInt(oyuncuId) },
        olayTipi: 'GOL',
        dakika: parseInt(dakika)
      });
      setSuccess('Gol başarıyla eklendi!');
      setTimeout(() => {
        onSuccess();
        onClose();
      }, 1500);
    } catch (err) {
      setError(err.hata || 'Gol eklenemedi');
    } finally {
      setLoading(false);
    }
  };
  
  const handleMacBaslat = async () => {
    try {
      setLoading(true);
      setError(null);
      await editorAPI.startMatch(match.id);
      setSuccess('Maç başlatıldı!');
      setTimeout(() => {
        onSuccess();
        onClose();
      }, 1500);
    } catch (err) {
      setError(err.hata || 'Maç başlatılamadı');
    } finally {
      setLoading(false);
    }
  };
  
  const handleMacBitir = async () => {
    try {
      setLoading(true);
      setError(null);
      await editorAPI.finishMatch(match.id);
      setSuccess('Maç sonuçlandırıldı!');
      setTimeout(() => {
        onSuccess();
        onClose();
      }, 1500);
    } catch (err) {
      setError(err.hata || 'Maç sonuçlandırılamadı');
    } finally {
      setLoading(false);
    }
  };
  
  if (!match) return null;
  
  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        <Typography variant="h6">
          {match.evSahibiTakim?.ad} vs {match.deplasmanTakim?.ad}
        </Typography>
        <Typography variant="caption" color="textSecondary">
          {match.tarih} {match.saat}
        </Typography>
        {!canManageMatch && (
          <Alert severity="warning" sx={{ mt: 1 }}>
            Maç henüz başlamadı. Maç başlangıç saatinden sonra işlem yapabilirsiniz.
          </Alert>
        )}
      </DialogTitle>
      
      <DialogContent>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
        
        <Box sx={{ mb: 2 }}>
          <Button
            variant={tab === 'score' ? 'contained' : 'outlined'}
            onClick={() => setTab('score')}
            sx={{ mr: 1 }}
          >
            Skor Güncelle
          </Button>
          <Button
            variant={tab === 'goal' ? 'contained' : 'outlined'}
            onClick={() => setTab('goal')}
            sx={{ mr: 1 }}
            disabled={!canManageMatch}
          >
            Gol Ekle
          </Button>
          <Button
            variant={tab === 'control' ? 'contained' : 'outlined'}
            onClick={() => setTab('control')}
            disabled={!canManageMatch}
          >
            Maç Kontrolü
          </Button>
        </Box>
        
        {tab === 'score' && (
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField
                fullWidth
                type="number"
                label={`${match.evSahibiTakim?.ad} Skor`}
                value={evSahibiSkor}
                onChange={(e) => setEvSahibiSkor(e.target.value)}
                disabled={!canManageMatch}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                type="number"
                label={`${match.deplasmanTakim?.ad} Skor`}
                value={deplasmanSkor}
                onChange={(e) => setDeplasmanSkor(e.target.value)}
                disabled={!canManageMatch}
              />
            </Grid>
            <Grid item xs={12}>
              <Button
                fullWidth
                variant="contained"
                color="primary"
                onClick={handleSkorGuncelle}
                disabled={loading || !canManageMatch}
                startIcon={<SportsSoccerIcon />}
              >
                Skoru Güncelle
              </Button>
            </Grid>
          </Grid>
        )}
        
        {tab === 'goal' && (
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Gol Atan Oyuncu</InputLabel>
                <Select
                  value={oyuncuId}
                  onChange={(e) => setOyuncuId(e.target.value)}
                  label="Gol Atan Oyuncu"
                >
                  {players.map((player) => (
                    <MenuItem key={player.id} value={player.id}>
                      {player.ad} {player.soyad}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                type="number"
                label="Dakika"
                value={dakika}
                onChange={(e) => setDakika(e.target.value)}
                placeholder="45"
              />
            </Grid>
            <Grid item xs={12}>
              <Button
                fullWidth
                variant="contained"
                color="success"
                onClick={handleGolEkle}
                disabled={loading}
                startIcon={<SportsSoccerIcon />}
              >
                Gol Ekle
              </Button>
            </Grid>
          </Grid>
        )}
        
        {tab === 'control' && (
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Button
                fullWidth
                variant="contained"
                color="primary"
                onClick={handleMacBaslat}
                disabled={loading}
                startIcon={<PlayArrowIcon />}
              >
                Maçı Başlat
              </Button>
            </Grid>
            <Grid item xs={12}>
              <Button
                fullWidth
                variant="contained"
                color="error"
                onClick={handleMacBitir}
                disabled={loading}
                startIcon={<StopIcon />}
              >
                Maçı Sonuçlandır
              </Button>
            </Grid>
          </Grid>
        )}
      </DialogContent>
      
      <DialogActions>
        <Button onClick={onClose} disabled={loading}>
          Kapat
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default MatchActionsDialog;

