# Reserva-Facil

### Proposta de projeto pós-graduação FIAP - Hackathon

### Tópicos

- [Descrição do projeto](#descrição-do-projeto)
- [Funcionalidades](#funcionalidades)
- [Ferramentas utilizadas](#ferramentas-utilizadas)
- [Acesso ao projeto](#acesso-ao-projeto)
- [Abrir e rodar o projeto](#abrir-e-rodar-o-projeto)
- [Desenvolvedores](#desenvolvedores)

## Descrição do projeto

<p align="justify">
Este projeto tem como objetivo a implementação de um sistema de reservas e gerenciamento de estoque, com a finalidade de reduzir filas e tornar o processo de aquisição de medicamentos pelo SUS mais eficiente.
</p>

## Funcionalidades

`Funcionalidade 1:` CRUD Beneficiários
- Apenas o Beneficiario logado pode consultar/alterar o seu próprio cadastro;

`Funcionalidade 2:` CRUD Operadores
- Apenas o Operador logado pode consultar/alterar o seu próprio cadastro;

`Funcionalidade 3:` CRUD Prestadores
- Apenas Operadores podem alterar/remover Prestadores que estejam relacionados ao seu cadastro;
- Tanto Operadores quanto Beneficiarios podem realizar as diversas consultas;

`Funcionalidade 4:` CRUD Medicamentos/Documentos
- Apenas Operadores podem cadastrar/alterar/consultar/remover medicamentos relacionados ao prestador que o Operador está empregrado;

`Funcionalidade 5:` Autenticação token JWT para Beneficiarios e Operadores;
- Login via cns e senha(senha padrão = "123");

`Funcionalidade 6:` Busca de prestadores por cidade e medicamento;
- Tanto Operadores quanto Beneficiarios podem realizar a consulta;

`Funcionalidade 7:` Busca de horarios disponiveis de um Prestador;
- Tanto Operadores quanto Beneficiarios podem realizar a consulta;

`Funcionalidade 8:` Agendamento de reservas de retirada de medicamento
- Apenas Beneficiarios podem realizar agendamentos de reservas;
- A data da reserva deve ser futura e com minutagem(00, 15, 30 ou 45);
- Apenas 3 reservas por horario são disponiveis por prestador;
- O medicamento deve estar disponivel no estoque, pertencer ao prestador informado e estar de acordo com o tipo de medicamento permitido para o Beneficiario;


## Ferramentas utilizadas

<a href="https://www.java.com" target="_blank"> 
    <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="Java" width="40" height="40"/> 
</a>
<a href="https://spring.io/" target="_blank"> 
    <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original.svg" alt="Spring" width="40" height="40"/> 
</a>
<a href="https://www.postman.com/" target="_blank"> 
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/postman/postman-original.svg" alt="Postman" width="40" /> 
</a>
<a href="https://junit.org/junit5/" target="_blank"> 
    <img src="https://camo.githubusercontent.com/47ab606787e47aee8033b92c8f1d05c0e74b9b81904550f35a8f54e39f6c993b/68747470733a2f2f6a756e69742e6f72672f6a756e6974352f6173736574732f696d672f6a756e6974352d6c6f676f2e706e67" alt="JUnit" width="40" height="40"/> 
</a>
<a href="https://www.postgresql.org/" target="_blank"> <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/postgresql/postgresql-plain.svg" width="40"/> </a>
<a href="https://www.docker.com/" target="_blank">
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/docker/docker-plain.svg" width="40"/>
</a>



## Acesso ao projeto

Você pode [acessar o código fonte do projeto](https://github.com/Grupo23TC/reserva-facil).

## Abrir e rodar o projeto

Para um teste mais rápido suba a aplicação com o perfil spring **DEV** que utiliza um banco de dados H2 e conta com um seed sql.

Caso queria utilizar o banco Postgres, mude o perfil spring para **HOMOLOG**. Será necessário ter o Docker Hub instalado e rodando, então rode o comando ``docker-compose up``.
Será criada a imagem reserva-facil:homolog e um container com a aplicação e banco de dados rodando.

Para você acessar o Swagger, basta, enquanto o projeto estiver em execução, acessar o link: http://localhost:8080/swagger-ui/index.html

Ou você pode utilizar a collection do Postman disponível no [Drive](https://drive.google.com/drive/u/1/folders/1AJozTwnxzFJrdN49s9lKb8Sivb8gsoPE)

### CUIDADO
Como estamos o selenium para fazer o login na aplicação e obter o token jwt para fazermos corretamente os testes de controller de integração da nossa aplicação, recomendamos usar o navegador Google Chrome na sua versão 134.0.6998.36 para que não haja falhas nesses testes. Para facilitar o uso do selenium, colocamos o webdriver do Google Chrome dentro dos pacotes de testes, isso deve facilitar a execução do mesmo e evitar conflitos de versões, erros ou outros imprevistos que podem surgir.

## Desenvolvedores

<table align="center">
  <tr>
    <td align="center">
      <div>
        <img src="https://avatars.githubusercontent.com/caiotfernandes" width="120px;" alt="Foto no GitHub" class="profile"/><br>
          <b> Caio Fernandes  </b><br>
            <a href="https://www.linkedin.com/in/caio-t%C3%A1rraga-fernandes-524373126/" alt="Linkedin"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" height="20"></a>
            <a href="https://github.com/caiotfernandes" alt="Github"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" height="20"></a>
      </div>
    </td>

   <td align="center">
      <div>
        <img src="https://avatars.githubusercontent.com/davialvs" width="120px;" alt="Foto no GitHub" class="profile"/><br>
          <b> Davi Alves  </b><br>
            <a href="https://www.linkedin.com/in/davi-alves-dev/" alt="Linkedin"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" height="20"></a>
            <a href="https://github.com/davialvs" alt="Github"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" height="20"></a>
      </div>
    </td>
<td align="center">
      <div>
        <img src="https://avatars.githubusercontent.com/LucasFrancoBN" width="120px;" alt="Foto no GitHub" class="profile"/><br>
          <b> Lucas Franco   </b><br>
            <a href="https://www.linkedin.com/in/lucas-franco-barbosa-navarro-a51937221/" alt="Linkedin"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" height="20"></a>
            <a href="https://github.com/LucasFrancoBN" alt="Github"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" height="20"></a>
      </div>
    </td>
  <td align="center">
      <div>
        <img src="https://avatars.githubusercontent.com/lucasctteixeira" width="120px;" alt="Foto no GitHub" class="profile"/><br>
          <b> Lucas Teixeira </b><br>
            <a href="https://www.linkedin.com/in/lucas-c-teixeira/" alt="Linkedin"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" height="20"></a>
            <a href="https://github.com/lucasctteixeira" alt="Github"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" height="20"></a>
      </div>
    </td>
</tr>
</table>
