package com.viaLaser.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.viaLaser.enums.TipoPessoa;
import org.viaLaser.model.Pessoa;
import org.viaLaser.model.PessoaFisica;
import org.viaLaser.model.PessoaJuridica;
import org.viaLaser.service.PessoaService;

import com.viaLaser.util.Util;

@Named
@ViewScoped
public class PessoaController implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	private PessoaService pessoaService;
	
	@Produces
	@Named
	private Pessoa pessoa;
	
	private TipoPessoa tipoPessoa;
	
	private LazyDataModel<Pessoa> lazyListDataModel;

	@Named
	private String telefoneTemp;
	
	private boolean editar;
	
	@PostConstruct
	private void init() {
		if(pessoa == null){
			Object pessoa = Util.getAtributoSessao("pessoa");
			if(pessoa != null){
				this.pessoa = (Pessoa) pessoa;
			}
		}
		
		if(pessoa != null && tipoPessoa == null){
			if(pessoa instanceof PessoaFisica){
				tipoPessoa = TipoPessoa.PESSOA_FISICA;
			}else{
				tipoPessoa = TipoPessoa.PESSOA_JURIDICA;
			}
			editar = true;
		}
	}
	
	public String adicionar(){
		return "novaPessoa";
	}
	
	public void remover(){
		pessoaService.remover(pessoa);
		pessoa = null;
	}
	
	public String salvar(){
		Util.removeAtributoSessao("pessoa");
		pessoaService.save(pessoa);
		if(pessoa instanceof PessoaFisica){
			pessoa = new PessoaFisica();
		}else{
			pessoa = new PessoaJuridica();
		}
		return "listarPessoas";
	}
	
	public String editar(){
		Util.addAtributoSessao("pessoa", pessoa);
		return "novaPessoa";
	}
	
	public boolean renderizar(int tipo){
		if(tipoPessoa != null && tipoPessoa.ordinal() == tipo){
			return true;
		}
		return false;
	}
	
	public LazyDataModel<Pessoa> getLazyDataModel() {
		if (lazyListDataModel == null) {

			lazyListDataModel = new LazyDataModel<Pessoa>() {

				@Override
				public Pessoa getRowData(String rowKey) {
					return pessoaService.findById(Long.valueOf(rowKey));
				}

				@Override
				public Object getRowKey(Pessoa al) {
					return al.getId();
				}

				@Override
				public List<Pessoa> load(int first, int pageSize, String order, SortOrder so,Map<String, Object> where) {

					String orderByParam = (order != null) ? order : "id";
					String orderParam = ("ASCENDING".equals(so.name())) ? "asc" : "desc";
					List<Pessoa> ol = pessoaService.find(first, pageSize, orderByParam, orderParam, where);

					if (ol != null ) {
						lazyListDataModel.setRowCount((int) pessoaService.count());
						return ol;
					}else{
						this.setRowCount((int) pessoaService.count());
						return null;
					}
				}
			};
			lazyListDataModel.setRowCount((int) pessoaService.count());

		}

		return lazyListDataModel;

	}
	
	public  boolean isPessoaFisica(Pessoa pessoa){
		if(pessoa instanceof PessoaFisica){
			return true;
		}
		return false;
	}

	public ArrayList<SelectItem> getTipoPessoaSelectIItem() {
		ArrayList<SelectItem> items = new ArrayList<SelectItem>();
		try {
			for (TipoPessoa tp : TipoPessoa.values()) {
				items.add(new SelectItem(tp, tp.getName()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}

	public PessoaService getPessoaService() {
		return pessoaService;
	}

	public void setPessoaService(PessoaService pessoaService) {
		this.pessoaService = pessoaService;
	}

	public TipoPessoa getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(TipoPessoa tipoPessoa) {
		if(tipoPessoa != null && tipoPessoa.equals(TipoPessoa.PESSOA_FISICA)){
			if(pessoa == null || pessoa.getId() == null || pessoa.getId() ==0){
				pessoa = new PessoaFisica();
			}
		}
		if(tipoPessoa != null && tipoPessoa.equals(TipoPessoa.PESSOA_JURIDICA)){
			if(pessoa == null || pessoa.getId() == null || pessoa.getId() ==0){
				pessoa = new PessoaJuridica();
			}
			
		}
		this.tipoPessoa = tipoPessoa;
	}

	public void adicionarTelefone(){
		PessoaJuridica pj = (PessoaJuridica) pessoa;
		if(pj.getTelefones() == null){
			pj.setTelefones(new ArrayList<String>());
		}
		if(telefoneTemp != null && !telefoneTemp.equalsIgnoreCase("")){
			pj.getTelefones().add(telefoneTemp);
		}
		
		telefoneTemp = "";
	}
	
	public void removerTelefone(int indice){
		PessoaJuridica pj = (PessoaJuridica) pessoa;
		pj.getTelefones().remove(indice);
	}
	
	public boolean isPessoaSelecionada(){
		if(pessoa != null){
			return true;
		}else{
			return false;
		}
	}
 
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getTelefoneTemp() {
		return telefoneTemp;
	}

	public void setTelefoneTemp(String telefoneTemp) {
		this.telefoneTemp = telefoneTemp;
	}

	public boolean isEditar() {
		return editar;
	}

	public void setEditar(boolean editar) {
		this.editar = editar;
	}
	
}
