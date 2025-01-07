class SelectionsController < ApplicationController
  before_action :set_selection, only: [:show, :edit, :update, :destroy]
  before_action :set_cart_display, only: [:index, :create, :update, :destroy]

  def set_cart_display
    session[:cart_display]='cell'
  end

  def write_selection(items)

    project_dir =  Pathname.new(APP_CONFIG[:user_data_dir]) + @project.user_id.to_s + @project.key
    selection_dir = project_dir + 'selections'
    Dir.mkdir selection_dir if !File.exist?(selection_dir)
    file = @selection.id.to_s + ".txt"
    File.open(selection_dir + file, 'w') do |f|
      f.write(items.join("\n"))
    end
    session[:selections][@selection.id]={:item_list => items.sort, :edited => @selection.edited}
  end

  # GET /selections
  # GET /selections.json
  def index
    @selections = Selection.all
  end

  # GET /selections/1
  # GET /selections/1.json
  def show
  end

  # GET /selections/new
  def new
    @selection = Selection.new
  end

  # GET /selections/1/edit
  def edit
  end

  def get_all_cells
    h_cells = {}
    project_dir =  Pathname.new(APP_CONFIG[:user_data_dir]) + @project.user_id.to_s + @project.key
    input_file = project_dir + 'parsing' + 'output.tab'
    File.open(input_file, 'r') do |f|
      f.gets.chomp.split(/\t/)[1..-1].map{|e| 
        h_cells[e]=1
      }
    end
    return h_cells
  end
  
  # POST /selections
  # POST /selections.json
  def create
    @selection = Selection.new(selection_params)
   
    @project = Project.where(:key => params[:project_key]).first
 
    if analyzable?(@project)
      label = ""
      cluster = nil
      
      h_all_cells = get_all_cells()
      
      if @selection.obj_id
        obj = nil
        if @selection.selection_type_id == 1
          obj = Cluster.where(:id => @selection.obj_id).first
        elsif @selection.selection_type_id == 2
          obj = DiffExpr.where(:id => @selection.obj_id).first
        end
        @selection.label = ["Cluster", (obj.num || '-').to_s + "." + @selection.obj_num.to_s].join(" ")      
      else
        last_selection = Selection.where(:project_id => @project.id, :obj_id => nil).order("manual_num desc").select{|e| e.manual_num != nil}.first
        @selection.manual_num = (last_selection) ? (last_selection.manual_num + 1) : 1
        @selection.label = ["Manual", @selection.manual_num].join(" ")
      end
      items = params[:list_of_items].split(/\s*[;\,]\s*/).map{|e| e.split(/\s*\n+\s*/)}.flatten.uniq.reject { |c| !h_all_cells[c] }.sort
      @selection.project_id = @project.id
      @selection.nb_items = items.size
      @selection.md5 = Digest::MD5.hexdigest items.to_json
      @selection.user_id =  (current_user) ? current_user.id : 1
      #label text, --"Manual x / Cluster x.y"                                                        
      # manual_num int, -- identifier in case of manual selection else null                          
      #  obj_id int references clusters, -- null if manual selection                             
      #  obj_num int, --null if manual selection                                                
      #    nb_items int,
    #project_id int references projects,
      
      respond_to do |format|
        if @selection.save
          
          write_selection(items)
          session[:selections][@selection.id]={:item_list => items, :edited => false}
          
          format.html { render :partial => 'projects/cart', notice: 'Selection was successfully created.' }
          format.json { render :show, status: :created, location: @selection }
        else
          format.html { render :new }
          format.json { render json: @selection.errors, status: :unprocessable_entity }
        end
      end
    else
      #      render :nothing => true
      head :unprocessable_entity
    end
  end

  # PATCH/PUT /selections/1
  # PATCH/PUT /selections/1.json
  def update
    
    #    logger.debug("TEST: " + session[:selections].to_json)
    @project = @selection.project
    
    if analyzable_item?(@project, @selection) #authorized?
      h_all_cells = get_all_cells()
      items = params[:list_of_items].split(/\s*[;\,]\s*/).map{|e| e.split(/\s*\n+\s*/)}.flatten.uniq.reject { |c| !h_all_cells[c] }.sort
      
      respond_to do |format|
        
        h_selection_labels = {}
        @project.selections.each do |s|
          h_selection_labels[s.label]=1 if s.id !=@selection.id
        end
        h = {:nb_items => items.size}
        
        previous = session[:selections][@selection.id][:item_list]
        h[:edited] = true if previous != items
        h[:label] = params[:selection_label]
        h[:md5] = Digest::MD5.hexdigest items.to_json
        if !h_selection_labels[h[:label]] and @selection.update_attributes(h)
          write_selection(items)
        end
        format.html {  render :partial => "projects/cart", notice: 'Selection was successfully updated.' }
        format.json { render :show, status: :ok, location: @selection }
        # else
        #   format.html { render :edit }
        #   format.json { render json: @selection.errors, status: :unprocessable_entity }
        # end
      end
    else
      render :nothing => true
    end
  end
  
  # DELETE /selections/1
  # DELETE /selections/1.json
  def destroy
    @project = @selection.project
    if analyzable_item?(@project, @selection)
      project_dir =  Pathname.new(APP_CONFIG[:user_data_dir]) + @project.user_id.to_s + @project.key    
      selection_dir = project_dir + 'selections'
      file = selection_dir + (@selection.id.to_s + ".txt")
      File.delete(file) if File.exist?(file)
      @selection.destroy
      
      respond_to do |format|
        format.html { render :partial => "projects/cart" } #redirect_to selections_url, notice: 'Selection was successfully destroyed.' }
        format.json { head :no_content }
      end
    else
      render :nothing => true
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_selection
      @selection = Selection.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def selection_params
      params.fetch(:selection).permit(:project_id, :obj_id, :obj_num, :nb_items) if params[:selection]
    end
end
