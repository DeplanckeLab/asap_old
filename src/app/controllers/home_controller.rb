class HomeController < ApplicationController

  def index

    if current_user
      redirect_to :projects
    else
      render
    end
    
  end

  def support
    render :layout => false if params[:nolayout]
  end

  def benchmark
    render :layout => false if params[:nolayout]
  end

  def citation
     render :layout => false if params[:nolayout]
  end

  def about
  end

  def tutorial
    render :layout => false if params[:nolayout]
  end

  def file_format
    render :layout => false if params[:nolayout]
  end

  def faq
    render :layout => false if params[:nolayout]
  end

end
