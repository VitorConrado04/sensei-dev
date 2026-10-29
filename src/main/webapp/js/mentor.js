// SENSEI.DEV | área do mentor
// Os dados vêm do servlet e o JSP monta as telas. Aqui ficam só dois comportamentos
// que o servidor não resolve: abrir/fechar a janelinha de material (novo e editar) e trocar o perfil
// entre modo leitura e modo edição.

// ---------- Tela de material: janelinha (dialog) de novo material ----------
var dialogoMaterial = document.getElementById('dialog-material');

if (dialogoMaterial) {
    var formMaterial = dialogoMaterial.querySelector('form');

    // abre o diálogo como "novo" (sem id) ou como "editar" (com os valores do material)
    function abrirDialogoMaterial(material) {
        formMaterial.reset();                              // abre sempre com os campos limpos
        var editando = material !== null;
        document.getElementById('t-novo-material').textContent = editando ? 'Editar material' : 'Novo material';
        document.getElementById('btn-salvar-material').textContent = editando ? 'Salvar alterações' : 'Adicionar material';
        document.getElementById('material-acao').value = editando ? 'editar' : 'adicionar';
        document.getElementById('material-id').value = editando ? material.id : '';
        if (editando) {
            document.getElementById('material-titulo').value = material.titulo;
            document.getElementById('material-url').value = material.url;
            document.getElementById('material-descricao').value = material.descricao;
            document.getElementById('material-mentoria').value = material.mentoria;
        }
        dialogoMaterial.showModal();
    }

    document.getElementById('btn-add-material').addEventListener('click', function () {
        abrirDialogoMaterial(null);
    });

    // cada botão "Editar" carrega os dados do card nos atributos data-*
    var botoesEditar = document.querySelectorAll('.btn-editar-material');
    for (var i = 0; i < botoesEditar.length; i++) {
        botoesEditar[i].addEventListener('click', function (evento) {
            var d = evento.currentTarget.dataset;
            abrirDialogoMaterial({
                id: d.id,
                titulo: d.titulo || '',
                url: d.url || '',
                descricao: d.descricao || '',
                mentoria: d.mentoria || ''
            });
        });
    }

    document.getElementById('btn-cancelar-material').addEventListener('click', function () {
        dialogoMaterial.close();
    });

    // clicar no fundo escuro (fora da janelinha) também fecha
    dialogoMaterial.addEventListener('click', function (evento) {
        if (evento.target === dialogoMaterial) {
            dialogoMaterial.close();
        }
    });
}

// ---------- Tela de perfil: editar / cancelar ----------
var formPerfil = document.getElementById('form-perfil');

if (formPerfil) {
    // a classe "editando" faz o CSS esconder o que é só leitura e mostrar os campos
    document.getElementById('btn-editar').addEventListener('click', function () {
        formPerfil.classList.add('editando');
        formPerfil.elements['descricao'].focus();
    });

    // reset() devolve o texto e os checkboxes pro que o servidor tinha mandado
    document.getElementById('btn-cancelar-edicao').addEventListener('click', function () {
        formPerfil.reset();
        formPerfil.classList.remove('editando');
    });
}