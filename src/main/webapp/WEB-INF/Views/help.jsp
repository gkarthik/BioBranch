<style>
.switch-image{
background: #428BCA;
padding: 5px;
width: 30px;
height: 40px;
border-radius:5px;
}

.video-tutorial{
color: #FFF;
float: right;
}

.video-tutorial:hover{
color: #FFF;
}
</style>
<div class="container">
	<div class="row">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Choose a Dataset</h3>
			</div>
			<div class="panel-body">
				<p>
				<img src="./static/img/tutorials/dataset.png" align="left">
				A dataset can be visualized as a table in which each row represents an instance and each column, an attribute.<br>
				Once you login, you will find a list of datasets available on Branch for you to use. These will be the datasets that you can explore and use to build trees and other classifiers. 
				</p>
			</div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Building a Decision Tree <span class="btn btn-link video-tutorial"><span class="glyphicon glyphicon-facetime-video"></span> Video</span></h3>
			</div>
			<div class="panel-body">
				<p>  
				A decision tree can be built by picking a gene/non gene feature to split the dataset.
				There are two basic types of features that can be picked.   
				 <div class="col-sm-6">
			      <img src="./static/img/tutorials/tree_numerical.png" class="img-responsive" alt="Numerical">
			      <div class="caption">
			        <h3><img title="Switch to Genes" src="./static/img/dna.png" class="switch-image"> Numerical</h3>
			        <p>The decision tree splits the instances using a split value.</p>
			      </div>
			    </div>
			     <div class="col-sm-6">
			      <img src="./static/img/tutorials/tree_nominal.png" class="img-responsive" alt="Nominal">
			      <div class="caption">
			        <h3><img title="Switch to Genes" src="./static/img/doctor.png" class="switch-image"> Nominal</h3>
			        <p>The decision tree splits the instances based on the different values of the feature.</p>
			      </div>
			    </div>
			    You can view the class distribution and change the split point for numerical attributes by clicking on <button class="btn btn-primary">Class Distribution</button>. 
			    <br>
				</p>
			</div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Basic Settings <span class="btn btn-link video-tutorial"><span class="glyphicon glyphicon-facetime-video"></span> Video</span></h3>
			</div>
			<div class="panel-body">
			<div>
				<h4>Training and Testing Datasets</h4>
				<p>
				<img src="./static/img/tutorials/testtrain.png" width="600" align="left">
				 You can choose a training and testing dataset by clicking on the <button id="show-dataset-wrapper" class="btn btn-primary"><i class="glyphicon glyphicon-cog"></i></button> in the sidebar. 
				 Available Options:
				 </p>
				 <dl>
				 	<dt>Training Set</dt>
				 	<dd>Use the same dataset for both training and testing.</dd>
				 	<dt>Supplied Test Set</dt>
				 	<dd>A list of compatible datsets is shown. You can pick one to be used as a test dataset.</dd>
				 	<dt>Percentage Split</dt>
				 	<dd>Use a percentage of the dataset you picked for training and the rest for testing.</dd>
				 </dl>
				 </div>
				 <br>
				 <div>
				 <h4>Zoom Controls</h4>
				 <p>
				 <img src="./static/img/tutorials/zoom.png" align="left">
				Use <i class="glyphicon glyphicon-zoom-in"></i> to zoom in and <i class="glyphicon glyphicon-zoom-out"></i> to zoom out.
				You can uncheck Fit To Screen if you don't want the tree to fit within the screen.</p>
				</div>
				<br><br>
				<div>
				<h4>Accuracy, Area Under Curve and the Confusion Matrix</h4>
				<p>
					<img src="./static/img/tutorials/accauc.png" align="left">
					The decision tree is evaluated on the test set you pick and the corresponding accuracy, area under curve and confusion matrix are shown in the right side bar.  
				</p>
				</div>
			</div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Saving Trees Into Your Collections <span class="btn btn-link video-tutorial"><span class="glyphicon glyphicon-facetime-video"></span> Video</span></h3>
			</div>
			<div class="panel-body">
				<p>  
				Click on <button class="btn btn-primary" id="init_save_tree"><i class="glyphicon glyphicon-save"></i></button> to save your tree.
				You have an options to save the tree along with a comment. These comments will come in handy if you need to find your tree later on.
				In addition if you want to keep your tree private you can save it as a private tree and such trees will not be visible to other users. 
				You can view your saved trees in your profile. You can access your profile by clicking on your name in the right corner of the top bar.
				You can view your collections and trees built by other users. You can view other user's trees by clicking on <a href="#"><i class="glyphicon glyphicon-edit"></i></a>.
				The nodes added by you will correspond to your profile and this can be seen in the user thumbnail on the top right corner of each node. 
				</p>
			</div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Pathway Search <span class="btn btn-link video-tutorial"><span class="glyphicon glyphicon-facetime-video"></span> Video</span></h3>
			</div>
			<div class="panel-body">
				<p>  
				//Pathway Search 
				</p>
			</div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Build & Add Classifier <span class="btn btn-link video-tutorial"><span class="glyphicon glyphicon-facetime-video"></span> Video</span></h3>
			</div>
			<div class="panel-body">
				<p>  
				//Classifier
				</p>
			</div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Plot & Create Split <span class="btn btn-link video-tutorial"><span class="glyphicon glyphicon-facetime-video"></span> Video</span></h3>
			</div>
			<div class="panel-body">
				<p>  
				//Plot & Create Split
				</p>
			</div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Build & Add Custom Feature <span class="btn btn-link video-tutorial"><span class="glyphicon glyphicon-facetime-video"></span> Video</span></h3>
			</div>
			<div class="panel-body">
				<p>  
				//Custom Feature
				</p>
			</div>
		</div>
	</div>
</div>