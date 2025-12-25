import React, { useState } from 'react';
import { Box, Card, CardContent, TextField, Button, Typography, Alert, Stack } from '@mui/material';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from 'utils/api';
import AuthService from 'utils/authService';

const authService = new AuthService(authAPI);

function RegisterPage() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async () => {
    if (!username.trim() || !email.trim() || !password.trim() || !confirm.trim()) {
      setError('Tüm alanlar zorunludur.');
      return;
    }
    if (password !== confirm) {
      setError('Şifreler uyuşmuyor.');
      return;
    }
    try {
      setLoading(true);
      setError(null);
      await authService.register({
        kullaniciAdi: username.trim(),
        email: email.trim(),
        sifre: password.trim(),
      });
      navigate('/app');
    } catch (e) {
      let errorMessage = 'Kayıt başarısız. Lütfen tekrar deneyin.';

      if (e.data && e.data.hata) {
        errorMessage = e.data.hata;
      } else if (e.data && e.data.message) {
        errorMessage = e.data.message;
      } else if (e.message) {
        errorMessage = e.message;
      }

      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box display="flex" justifyContent="center" mt={6} px={2}>
      <Card sx={{ maxWidth: 420, width: '100%' }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>Kayıt Ol</Typography>
          <Stack spacing={2}>
            {error && <Alert severity="error">{error}</Alert>}
            <TextField
              label="Kullanıcı adı"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              fullWidth
            />
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
            <TextField
              label="Şifre (tekrar)"
              type="password"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
              fullWidth
            />
            <Button variant="contained" onClick={handleSubmit} disabled={loading}>
              {loading ? 'Gönderiliyor...' : 'Kayıt Ol'}
            </Button>
            <Typography variant="body2">
              Zaten hesabın var mı? <Link to="/login">Giriş yap</Link>
            </Typography>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}

export default RegisterPage;
