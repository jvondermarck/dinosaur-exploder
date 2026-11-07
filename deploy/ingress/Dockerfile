FROM nginx:stable-alpine@sha256:0e52bd054126cdc040b1276c0af347aeb02a41b9cfe305d6094767ac30e97949
COPY deploy/ingress/nginx.conf /etc/nginx/nginx.conf
COPY deploy/ingress/capacity.js deploy/ingress/capacity-test.js /etc/nginx/
COPY deploy/ingress/check.sh /etc/nginx/check.sh
COPY deploy/ingress/html /usr/share/nginx/html
RUN njs -m /etc/nginx/capacity-test.js \
    && sed -i 's/\r$//' /etc/nginx/check.sh \
    && sh -n /etc/nginx/check.sh
USER nginx
EXPOSE 8080
ENTRYPOINT ["nginx"]
CMD ["-g", "daemon off;"]
