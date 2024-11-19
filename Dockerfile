FROM ruby:2.6.6-buster
RUN apt-get update -qq && apt-get install -y \
  build-essential \
  libpq-dev \
  nodejs \
  npm \
  && rm -rf /var/lib/apt/lists/*
RUN touch /mimetypes
ENV FREEDESKTOP_MIME_TYPES_PATH=/mimetypes
WORKDIR /opt/asap-old
COPY src/Gemfile .
RUN gem update --system 3.3.22
RUN bundle install
COPY src/. .

#CMD ["puma", "-C", "config/puma.rb"]
CMD ["bash", "start.sh"]
#CMD ["bash"]