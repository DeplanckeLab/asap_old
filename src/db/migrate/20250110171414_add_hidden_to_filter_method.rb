class AddHiddenToFilterMethod < ActiveRecord::Migration[5.2]
  def change
    add_column :filter_methods, :hidden, :boolean
  end
end
