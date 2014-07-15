create table homosap  (
tax_id int ,
GeneID int ,
Symbol varchar(500) ,
LocusTag varchar(500),
Synonym varchar(1000),
dbXrefs varchar(500), 
chromosome varchar(20),
map_location varchar(500),
description varchar(500),
type_of_gene varchar(500),
Symbol_from_nomenclature_authority varchar(500), 
Full_name_from_nomenclature_authority varchar(500),
Nomenclature_status varchar,
Other_designations text,
Modification_date timestamp
);

9606 															tax_id 
1																GeneID 
A1BG 															Symbol 
-																LocusTag
A1B|ABG|GAB|HYST2477											Synonyms	
HGNC:5|MIM:138670|Ensembl:ENSG00000121410|HPRD:00726			dbXrefs	
19																chromosome	
19q13.4	alpha-1-B 												map_location
glycoprotein													description	
protein-coding													type_of_gene 	
A1BG															Symbol_from_nomenclature_authority	
alpha-1-B glycoprotein											Full_name_from_nomenclature_authority 	
O																Nomenclature_status 	
alpha-1B-glycoprotein											Other_designations	
20120909														Modification_date


 username varchar (100) not null, 
  series varchar(64) primary key, 
  token varchar(64) not null, 
  last_used timestamp not null
  
  \COPY homosap(tax_id ,GeneID ,Symbol,LocusTag ,Synonym ,dbXrefs,chromosome ,map_location ,description ,type_of_gene,Symbol_from_nomenclature_authority ,Full_name_from_nomenclature_authority ,Nomenclature_status,Other_designations ,Modification_date) from '/home/bob/Homo_sapiens.gene_info' ;
  
   \COPY HGI(tax_id ,GeneID ,Symbol,LocusTag ,Synonym ,dbXrefs,chromosome ,map_location ,description ,type_of_gene,Symbol_from_nomenclature_authority ,Full_name_from_nomenclature_authority ,Nomenclature_status,Other_designations ,Modification_date) from '/home/bob/Homo_sapiens.gene_info' ;
   
   
   
   
   
    insert into attribute(id,col_index,created,dataset,feature_id,name,relieff,updated) values (1,1,now(),'newdataset',12,'Vyshakh',1.0,now());
