import React, { useEffect, useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Stack,
  Divider,
  Chip,
  Button,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { usersAPI } from 'utils/api';

function ProfilePage() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const loadProfile = async () => {
    try {
      setLoading(true);
      const res = await usersAPI.me();
      setProfile(res);
      setError(null);
    } catch (e) {
      setError('Profil bilgileri alınamadı. Lütfen tekrar deneyin.');
      if (e?.response?.status === 401) {
        navigate('/login');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProfile();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (loading) {
    return <Typography>Yükleniyor...</Typography>;
  }

  if (error || !profile) {
    return <Typography color="error">{error || 'Profil bulunamadı'}</Typography>;
  }

  return (
    <Box mt={2}>
      <Typography variant="h4" gutterBottom>
        Profilim
      </Typography>
      <Card variant="outlined">
        <CardContent>
          <Stack spacing={1}>
            <Typography variant="h6">{profile.displayName}</Typography>
            <Typography color="text.secondary">{profile.email}</Typography>
            <Typography color="text.secondary">
              Üyelik tarihi: {profile.createdAt ? new Date(profile.createdAt).toLocaleDateString('tr-TR') : '-'}
            </Typography>
            <Stack direction="row" spacing={1}>
              <Chip label={`Takipçi: ${profile.followersCount || 0}`} />
              <Chip label={`Takip: ${profile.followingCount || 0}`} />
            </Stack>
          </Stack>
        </CardContent>
      </Card>

      <Box mt={3}>
        <Typography variant="h6" gutterBottom>Son Yorumlarım</Typography>
        {(profile.recentComments && profile.recentComments.length > 0) ? (
          profile.recentComments.map((c) => (
            <Card key={c.commentId} variant="outlined" sx={{ mb: 2 }}>
              <CardContent>
                <Stack spacing={1}>
                  <Stack direction="row" justifyContent="space-between" alignItems="center">
                    <Typography fontWeight="bold">{c.matchTitle || 'Yorum'}</Typography>
                    {c.matchId && (
                      <Button size="small" variant="text" href={`/app/matches/${c.matchId}`}>
                        Maça Git
                      </Button>
                    )}
                    {c.playerId && (
                      <Button size="small" variant="text" href={`/app/players/${c.playerId}`}>
                        Oyuncuya Git
                      </Button>
                    )}
                  </Stack>
                  <Typography>{c.message || c.icerik || ''}</Typography>
                  <Typography variant="caption" color="text.secondary">
                    {c.createdAt ? new Date(c.createdAt).toLocaleString('tr-TR') : ''}
                  </Typography>
                </Stack>
              </CardContent>
            </Card>
          ))
        ) : (
          <Typography color="text.secondary">Henüz yorumunuz yok.</Typography>
        )}
      </Box>
    </Box>
  );
}

export default ProfilePage;
