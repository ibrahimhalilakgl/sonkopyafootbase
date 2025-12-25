import React from 'react';
import { Box, Container, Stack, Typography, Divider } from '@mui/material';

function AppFooter() {
  return (
    <Box component="footer" sx={{ mt: 4, py: 3, bgcolor: 'background.paper' }}>
      <Container maxWidth="lg">
        <Divider sx={{ mb: 2 }} />
        <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" spacing={1}>
          <Typography variant="body2" color="text.secondary">
            © {new Date().getFullYear()} FootBase — Süper Lig verileri ve futbol analizi.
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Geri bildirim: info@footbase.local
          </Typography>
        </Stack>
      </Container>
    </Box>
  );
}

export default AppFooter;
