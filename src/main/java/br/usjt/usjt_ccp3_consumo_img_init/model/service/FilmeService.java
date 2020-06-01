package br.usjt.usjt_ccp3_consumo_img_init.model.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import br.usjt.usjt_ccp3_consumo_img_init.model.dao.FilmeDAO;
import br.usjt.usjt_ccp3_consumo_img_init.model.entity.Filme;
import br.usjt.usjt_ccp3_consumo_img_init.model.entity.Genero;
import br.usjt.usjt_ccp3_consumo_img_init.model.javabeans.Movie;
import br.usjt.usjt_ccp3_consumo_img_init.model.javabeans.Populares;

@Service
public class FilmeService {

	public static final String BASE_URL = "https://api.themoviedb.org/3";
	public static final String POPULAR = "/movie/popular";
	public static final String LANCAMENTO = "/movie/upcoming";
	public static final String POPULAR_PAR = "&language=pt-BR";
	public static final String API_KEY = "api_key=3c2961767239c4784df96d86dd0dfb5a";
	public static final String POSTER_URL = "https://image.tmdb.org/t/p/w300";
	@Autowired
	private FilmeDAO dao;
	
	public Filme buscarFilme(int id) throws IOException{
		return dao.buscarFilme(id);
	}
	
	@Transactional
	public Filme inserirFilme(Filme filme) throws IOException {
		int id = dao.inserirFilme(filme);
		filme.setId(id);
		return filme;
	}
	
	@Transactional
	public void atualizarFilme(Filme filme) throws IOException {
		dao.atualizarFilme(filme);
	}
	
	@Transactional
	public void excluirFilme(int id) throws IOException {
		dao.excluirFilme(id);
	}

	public List<Filme> listarFilmes(String chave) throws IOException{
		return dao.listarFilmes(chave);
	}

	public List<Filme> listarFilmes() throws IOException{
		return dao.listarFilmes();
	}
	
	@Transactional
	public List<Filme> baixarFilmesMaisPopulares() throws IOException{
		RestTemplate rest = new RestTemplate();
		String url = BASE_URL + POPULAR + "?" + API_KEY + POPULAR_PAR;
		System.out.println("url: " + url);
		Populares resultado = rest.getForObject(url, Populares.class);
		System.out.println("resultado: " + resultado);
		
		for(Movie movie: resultado.getResults()) {
			System.out.println(movie);
			
			Filme filme = new Filme();
			filme.setTitulo(movie.getTitle());
			filme.setDataLancamento(movie.getRelease_date());
			filme.setPopularidade(movie.getPopularity());
			filme.setPosterPath(POSTER_URL + movie.getPoster_path());
			filme.setDescricao(movie.getOverview());
			Genero genero = new Genero();
			genero.setId(movie.getGenre_ids()[0]);
			filme.setGenero(genero);
			
			dao.inserirFilme(filme);
			
		}
		return dao.listarFilmes();
	}
	@Transactional
	public List<Filme> baixarFilmesLancamento() throws IOException{
		RestTemplate rest = new RestTemplate();
		String url = BASE_URL + LANCAMENTO + "?" + API_KEY + POPULAR_PAR;
		System.out.println("url: " + url);
		Populares resultado = rest.getForObject(url, Populares.class);
		System.out.println("resultado: " + resultado);
		
		for(Movie movie: resultado.getResults()) {
			System.out.println(movie);
			
			Filme filme = new Filme();
			filme.setTitulo(movie.getTitle());
			filme.setDataLancamento(movie.getRelease_date());
			filme.setPopularidade(movie.getPopularity());
			filme.setPosterPath(POSTER_URL + movie.getPoster_path());
			filme.setDescricao(movie.getOverview());
			Genero genero = new Genero();
			genero.setId(movie.getGenre_ids()[0]);
			filme.setGenero(genero);
			
			dao.inserirFilme(filme);
			
		}
		return dao.listarFilmes();
	}	
	
	public void gravarImagem(ServletContext servletContext, Filme filme, MultipartFile file) throws IOException {
		if(!file.isEmpty()) {
			BufferedImage src = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
			
			String path = servletContext.getRealPath(servletContext.getContextPath());
			int lastIndex = path.lastIndexOf('/');
			path = path.substring(0, lastIndex != -1 ? lastIndex : path.length());
			String nomeArquivo = "img" + filme.getId()+ ".jpg";
			File destination = new File(path + File.separatorChar + "img" + File.separatorChar + nomeArquivo);
			filme.setPosterPath(destination.getPath() + File.separatorChar + nomeArquivo);
			
			atualizarFilme(filme);
			
			if(destination.exists()) {
				destination.delete();
			}
			ImageIO.write(src, "jpg", destination);
		}
	}
	
}











