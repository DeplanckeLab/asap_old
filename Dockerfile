FROM ruby:2.6.6-buster
RUN apt-get update -qq && apt-get install -y --no-install-recommends\
  build-essential \
  libpq-dev \
  nodejs \
  npm \
  wget \
  ca-certificates \
  libcurl4-openssl-dev \
  libxml2-dev \
  libssl-dev \
  libjpeg-dev \
  libpng-dev \
  libbz2-dev \
  liblzma-dev \
  libpcre2-dev \
  gfortran \
  libreadline-dev \
  zlib1g-dev \
  python2.7-dev \
  && apt-get clean && rm -rf /var/lib/apt/lists/*

# Install R 3.4.1 from source
RUN wget https://cloud.r-project.org/src/base/R-3/R-3.4.1.tar.gz && \
    tar -xf R-3.4.1.tar.gz && \
    cd R-3.4.1 && \
    ./configure --enable-R-shlib && \
    make && make install && \
    cd .. && rm -rf R-3.4.1*

COPY install_packages.R .
RUN mkdir log
#RUN Rscript install_packages.R 

# Optional: Verify installed packages
#RUN R -e "installed.packages()[, 'Package']"

#RUN touch /mimetypes
#ENV FREEDESKTOP_MIME_TYPES_PATH=/mimetypes
#WORKDIR /opt/asap-old
#COPY src/Gemfile .
#RUN gem update --system 3.3.22
#RUN bundle install
#COPY src/. .

#CMD ["puma", "-C", "config/puma.rb"]
#CMD ["bash", "start.sh"]
#CMD ["bash"]
