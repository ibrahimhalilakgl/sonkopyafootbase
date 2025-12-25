import React, { useState } from 'react';
import { Box, Card, CardContent, TextField, Button, Typography, Alert, Stack } from '@mui/material';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from 'utils/api';
import AuthService from 'utils/authService';

const authService = new AuthService(authAPI);

function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async () => {
    if (!email.trim() || !password.trim()) {
      setError('E-posta ve şifre gereklidir.');
      return;
    }
    try {
      setLoading(true);
      setError(null);
      await authService.login(email.trim(), password.trim());
      navigate('/app');
    } catch (e) {
      const message =
        (e.data && e.data.hata) ||
        (e.data && e.data.message) ||
        e.message ||
        'Giriş başarısız. Bilgilerinizi kontrol edin.';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box display="flex" justifyContent="center" mt={6} px={2}>
      <Card sx={{ maxWidth: 420, width: '100%' }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>Giriş Yap</Typography>
          <Stack spacing={2}>
            {error && <Alert severity="error">{error}</Alert>}
            <TextField
              label="E-posta"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              fullWidth
            />
            <TextField
              label="Şifre"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              fullWidth
            />
            <Button variant="contained" onClick={handleSubmit} disabled={loading}>
              {loading ? 'Gönderiliyor...' : 'Giriş Yap'}
            </Button>
            <Typography variant="body2">
              Hesabın yok mu? <Link to="/register">Kayıt ol</Link>
            </Typography>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}

export default LoginPage;
