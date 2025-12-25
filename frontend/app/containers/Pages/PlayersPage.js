import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  TextField,
  Box,
  Rating,
  Chip,
  Avatar,
  Pagination,
} from '@mui/material';
import { playersAPI } from 'utils/api';
import PapperBlock from 'dan-components/PapperBlock/PapperBlock';

function PlayersPage() {
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(1);
  const PAGE_SIZE = 50;

  useEffect(() => {
    loadPlayers();
  }, []);

  const loadPlayers = async () => {
    try {
      setLoading(true);
      const data = await playersAPI.list();
      setPlayers(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Oyuncular yüklenemedi:', error);
      setPlayers([]);
    } finally {
      setLoading(false);
    }
  };

  const filteredPlayers = players.filter(player => {
    if (!player) return false;
    const fullName = player.fullName || (player.ad && player.soyad ? `${player.ad} ${player.soyad}` : '');
    const teamName = player.team || (player.takim && player.takim.ad ? player.takim.ad : '');
    const searchLower = searchTerm.toLowerCase();
    return fullName.toLowerCase().includes(searchLower) || teamName.toLowerCase().includes(searchLower);
  });

  const pageCount = Math.ceil(filteredPlayers.length / PAGE_SIZE);
  const paginatedPlayers = filteredPlayers.slice(
    (page - 1) * PAGE_SIZE,
    page * PAGE_SIZE,
  );

  if (loading) {
    return (
      <Container>
        <Typography>Yükleniyor...</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg">
      <PapperBlock title="Oyuncular" icon="ios-people" desc="Tüm oyuncuları görüntüleyin ve puanlayın">
        <Box mb={3}>
          <TextField
            fullWidth
            label="Oyuncu Ara"
            variant="outlined"
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              setPage(1);
            }}
            placeholder="Oyuncu veya takım adı ile ara..."
          />
        </Box>
        <Grid container spacing={3}>
          {paginatedPlayers.map((player) => {
            const fullName = player.fullName || (player.ad && player.soyad ? `${player.ad} ${player.soyad}` : 'İsimsiz');
            const teamName = player.team || (player.takim && player.takim.ad ? player.takim.ad : null);
            const position = player.position || player.pozisyon || null;
            const imageUrl = player.imageUrl || player.fotograf || null;
            return (
              <Grid item xs={12} sm={6} md={4} key={player.id}>
                <Card>
                  <CardContent>
                    <Box display="flex" alignItems="center" mb={1.5}>
                      <Avatar
                        src={imageUrl || undefined}
                        alt={fullName}
                        sx={{ width: 56, height: 56, mr: 1.5 }}
                      >
                        {fullName ? fullName.charAt(0).toUpperCase() : '?'}
                      </Avatar>
                      <Box>
                        <Typography variant="h6" gutterBottom>
                          {fullName}
                        </Typography>
                        {teamName && (
                          <Chip label={teamName} size="small" color="primary" sx={{ mb: 1 }} />
                        )}
                      </Box>
                    </Box>
                    {position && (
                      <Typography variant="body2" color="textSecondary" gutterBottom>
                        Pozisyon: {position}
                      </Typography>
                    )}
                    {player.averageRating !== undefined && (
                      <Box mt={2}>
                        <Typography variant="body2" gutterBottom>
                          Ortalama Puan:
                        </Typography>
                        <Rating value={player.averageRating} precision={0.1} readOnly />
                        <Typography variant="caption" color="textSecondary">
                          ({player.averageRating.toFixed(1)}) - {player.ratingCount || 0} değerlendirme
                        </Typography>
                      </Box>
                    )}
                    <Box mt={2}>
                      <Button
                        variant="contained"
                        color="primary"
                        fullWidth
                        href={`/app/players/${player.id}`}
                      >
                        Detayları Gör
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            );
          })}
        </Grid>
        {filteredPlayers.length === 0 && (
          <Box textAlign="center" py={4}>
            <Typography variant="h6" color="textSecondary">
              Oyuncu bulunamadı
            </Typography>
          </Box>
        )}
        {filteredPlayers.length > 0 && (
          <Box display="flex" justifyContent="center" mt={4}>
            <Pagination
              count={pageCount}
              page={page}
              onChange={(_, value) => setPage(value)}
              color="primary"
              showFirstButton
              showLastButton
              siblingCount={1}
              boundaryCount={1}
            />
          </Box>
        )}
      </PapperBlock>
    </Container>
  );
}

export default PlayersPage;
