class SupportsController < ApplicationController
  before_action :set_support, only: [:show, :edit, :update, :destroy]

  # GET /supports
  # GET /supports.json
  def index
    @supports = Support.all
  end

  # GET /supports/1
  # GET /supports/1.json
  def show
  end

  # GET /supports/new
  def new
    @support = Support.new
  end

  # GET /supports/1/edit
  def edit
  end

  # POST /supports
  # POST /supports.json
  def create
    @support = Support.new(support_params)
    @support.user_id = current_user.id
    respond_to do |format|
      if @support.save
        format.html { redirect_to root_path, #@support
          notice: 'Support message was successfully sent.' }
        format.json { render :show, status: :created, location: @support }
      else
        format.html { render :new }
        format.json { render json: @support.errors, status: :unprocessable_entity }
      end
    end
  end

  # PATCH/PUT /supports/1
  # PATCH/PUT /supports/1.json
  def update
    respond_to do |format|
      if @support.update(support_params)
        format.html { redirect_to @support, notice: 'Support was successfully updated.' }
        format.json { render :show, status: :ok, location: @support }
      else
        format.html { render :edit }
        format.json { render json: @support.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /supports/1
  # DELETE /supports/1.json
  def destroy
    if admin? or (current_user and current_user.id == @support.user_id)
      @support.destroy
      respond_to do |format|
        format.html { 
          redirect_to edit_user_registration_path, notice: 'Support message was successfully deleted.'
          #redirect_to supports_url, notice: 'Support was successfully destroyed.'
        }
        format.json { head :no_content }
      end
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_support
      @support = Support.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def support_params
       params.fetch(:support).permit(:name, :affiliation, :message)
    end
end
