FROM nginx:stable

# unsere Site-Config
COPY Docker/default.conf /etc/nginx/conf.d/default.conf

# Angular build output
COPY dist/menu-assistent-website/browser/ /usr/share/nginx/html/browser/
