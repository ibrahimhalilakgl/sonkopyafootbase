import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Card,
  CardActionArea,
  CardContent,
  Typography,
  TextField,
  Box,
  Chip,
  Avatar,
  Stack,
} from '@mui/material';
import { teamsAPI } from 'utils/api';
import PapperBlock from 'dan-components/PapperBlock/PapperBlock';

function TeamsPage() {
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadTeams();
  }, []);

  const loadTeams = async () => {
    try {
      setLoading(true);
      const data = await teamsAPI.list();
      setTeams(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Takımlar yüklenemedi:', error);
      setTeams([]);
    } finally {
      setLoading(false);
    }
  };

  const filteredTeams = teams.filter((team) => {
    if (!team) return false;
    const teamName = team.name || team.ad || '';
    const teamCode = team.code || null;
    const searchLower = searchTerm.toLowerCase();
    return teamName.toLowerCase().includes(searchLower) || (teamCode && teamCode.toLowerCase().includes(searchLower));
  });

  if (loading) {
    return (
      <Container>
        <Typography>Yükleniyor...</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg">
      <PapperBlock title="Takımlar" icon="ios-people" desc="Tüm takımları görüntüleyin">
        <Box mb={3}>
          <TextField
            fullWidth
            label="Takım Ara"
            variant="outlined"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Takım adı veya kod ile ara..."
          />
        </Box>
        <Grid container spacing={3}>
          {filteredTeams.map((team) => {
            const teamName = team.name || team.ad || 'İsimsiz';
            const teamLogo = team.logoUrl || team.logo_url || team.logo || null;
            const teamCode = team.code || null;
            const teamShortName = team.shortName || null;
            const displayName = teamName.substring(0, 2).toUpperCase();
            return (
              <Grid item xs={12} sm={6} md={4} key={team.id}>
                <Card>
                  <CardActionArea href={`/app/teams/${team.id}`}>
                    <CardContent>
                      <Box display="flex" alignItems="center" mb={2}>
                        <Avatar
                          src={teamLogo || undefined}
                          alt={teamName}
                          sx={{ width: 56, height: 56, mr: 2, bgcolor: 'primary.main' }}
                        >
                          {displayName}
                        </Avatar>
                        <Box>
                          <Typography variant="h6">
                            {teamName}
                          </Typography>
                          <Stack direction="row" spacing={1} alignItems="center">
                            {teamCode && (
                              <Chip label={teamCode} size="small" color="primary" />
                            )}
                            {teamShortName && (
                              <Chip label={teamShortName} size="small" variant="outlined" />
                            )}
                          </Stack>
                        </Box>
                      </Box>
                    </CardContent>
                  </CardActionArea>
                </Card>
              </Grid>
            );
          })}
        </Grid>
        {filteredTeams.length === 0 && (
          <Box textAlign="center" py={4}>
            <Typography variant="h6" color="textSecondary">
              Takım bulunamadı
            </Typography>
          </Box>
        )}
      </PapperBlock>
    </Container>
  );
}

export default TeamsPage;
