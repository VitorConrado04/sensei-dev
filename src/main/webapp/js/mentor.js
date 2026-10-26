// SENSEI.DEV | área do mentor
// Os dados vêm do servlet e o JSP monta as telas. Aqui ficam só dois comportamentos
// que o servidor não resolve: abrir/fechar a janelinha de material e trocar o perfil
// entre modo leitura e modo edição.

// ---------- Tela de material: janelinha (dialog) de novo material ----------
var dialogoMaterial = document.getElementById('dialog-material');

if (dialogoMaterial) {
    document.getElementById('btn-add-material').addEventListener('click', function () {
        dialogoMaterial.querySelector('form').reset();   // abre sempre com os campos limpos
        dialogoMaterial.showModal();
    });

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
