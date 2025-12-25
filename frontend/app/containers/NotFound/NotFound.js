import React from 'react';
import { Helmet } from 'react-helmet';
import brand from 'dan-api/dummy/brand';
import { ErrorWrap } from 'dan-components';

const title = brand.name + ' - Sayfa Bulunamadı';
const description = brand.desc;

const NotFound = () => (
  <div>
    <Helmet>
      <title>{title}</title>
      <meta name="description" content={description} />
      <meta property="og:title" content={title} />
      <meta property="og:description" content={description} />
      <meta property="twitter:title" content={title} />
      <meta property="twitter:description" content={description} />
    </Helmet>
    <ErrorWrap title="404" desc="Üzgünüz, Sayfa Bulunamadı :(" />
  </div>
);

export default NotFound;
